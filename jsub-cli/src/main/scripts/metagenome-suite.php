#!/usr/bin/php
<?php


require_once 'Console/CommandLine.php';
require_once 'sai/org/sai/file/bowtie/BowtieStat.php';
require_once 'sai/org/sai/file/bowtie/Bowtie2Stat.php';


/*
 * Create command line parser object.
 */
$parser = new Console_CommandLine();
$parser->description = <<<DESC
A postprocess tool for compound metagenome suite.
It aggregates logs and result files into one set of files.
DESC;
$parser->version = '1.1';

/*
 * Describe command line options and arguments.
 */
$parser->addOption('pattern', array(
'long_name'   => '--pattern',
'short_name'  => '-p',
'description' => 'directory pattern to search within',
'action'      => 'StoreString'
));
$parser->addOption('name', array(
'long_name'   => '--output-name',
'short_name'  => '-o',
'description' => 'output files set name prefix',
'action'      => 'StoreString'
));

/*
 * Set evironment variables.
 */
$workDir = getcwd();

try {
    $cli = $parser->parse();

    /*
     * Create iterator for traverse through picked directories.
    */
    if ($cli->options['pattern'] === null) {
        $dirIterator = new DirectoryIterator($workDir);
    } else {
        $regexp      = '/' . $cli->options['pattern'] . '/';
        $dirIterator = new RegexIterator(new DirectoryIterator($workDir), $regexp);
    }


    /*
     * Loop through directories and results files within.
     */
    $files = array();
    foreach ($dirIterator as $dir) {
        if (! $dir->isDir() || $dir->isDot() || FileIterator::isHidden($dir->getFilename())) {
            continue;
        }

        $fileIterator = new FileIterator($dir->getPathname() . DIRECTORY_SEPARATOR . 'output');
        foreach ($fileIterator as $file) {
            $files[] = $file->getPathname();
        }
    }

    if ($cli->options['name'] === null) {
        $prefix = basename($workDir);
    } else {
        $prefix = $cli->options['name'];
    }

    /*
     * Copy and process results from projects directories to suite directory.
     */
    $outputs = array();
    foreach ($files as $index => $pathname) {
        preg_match('/^.*_\d+\.(\w+)\.txt$/', $pathname, $matches);
        $key = $matches[1];
        if (! array_key_exists($key, $outputs)) {

            $input  = fopen($pathname, 'rb');
            $output = fopen($workDir . DIRECTORY_SEPARATOR . $prefix . '.' . $key . '.txt', 'wb');

            fwrite($output, fgets($input));
            fclose($input);

            $outputs[$key] = $output;
        }
    }

    $results = array();
    foreach ($files as $pathname) {
        preg_match('/^.*_\d+\.(\w+)\.txt$/', $pathname, $matches);
        $key = $matches[1];

        $file = fopen($pathname, 'rb');
        // skip first line - file header
        fgets($file);
        while (($string = fgets($file)) !== false) {
            $results[$key][] = $string;
        }
        fclose($file);
    }

    /*
     * Sort output data by sample name and write to files.
     */
    foreach ($results as $key => & $result) {
        usort($result, function($a, $b) {
            $x = substr($a, 0, strpos($a, "\t"));
            $y = substr($b, 0, strpos($b, "\t"));

            return strcmp($x, $y);
        });

        foreach ($result as $string) {

            $data   = explode("\t", trim($string));
            $id     = $data[0];
            $values = array_slice($data, 1);
            $norma  = array_map(function($x, $sum) {
                return $x / $sum * 100;
            }, $values, array_fill(0, count($values), array_sum($values)));

            fwrite($outputs[$key], $id . "\t" . implode("\t", $norma) . "\n");
        }

        fclose($outputs[$key]);
    }


    /*
     * Process bowtie mapping stats and write its to common file as table.
     */
    $result = $workDir . DIRECTORY_SEPARATOR . $prefix . '.stats.txt';
    $output = fopen($result, 'wb');

    if ($output) {
        fwrite($output, "name\tmapped-to-human\tmapped-to-reference\tn-filtered-reads\tn-reads-total\tfiltered-reads-percent\n");

        $outputStrings = array();
        foreach ($dirIterator as $dir) {
            if (! $dir->isDir() || $dir->isDot() || FileIterator::isHidden($dir->getFilename())) {
                continue;
            }

            $humanBowtieStats = BowtieStatsFinder::getBowtieStatsLogFile($dir->getPathname(), BowtieStatsFinder::HUMAN_FILE);
            if (! $humanBowtieStats || ! $humanMappingPercent = $humanBowtieStats->getAlignedReadsPercent()) {
                $humanMappingPercent = 'NA';
            }

            $referenceBowtieStats = BowtieStatsFinder::getBowtieStatsLogFile($dir->getPathname(), BowtieStatsFinder::REFERENCE_FILE);
            if (! $referenceBowtieStats || ! $refernceMappingPercent = $referenceBowtieStats->getAlignedReadsPercent()) {
                $relativeToUnmappedMappingPercent = 'NA';
            } else {
                $relativeToUnmappedMappingPercent = round((100 - $humanMappingPercent) * $refernceMappingPercent / 100, 2);
            }

            // search for reads filtering stats
            $nReadsTotal     = 'NA';
            $nReadsFiltered  = $humanBowtieStats ? $humanBowtieStats->getProcessedReadsCount() : 'NA';
            $percentFiltered = 'NA';

            $path     = $dir->getPathname() . DIRECTORY_SEPARATOR . 'log';
            $file     = FileIterator::getFileByPattern('/reads-filter\.pl\.o\\d+/', $path);
            $filepath = $path . DIRECTORY_SEPARATOR . $file;

            if ($file == null) {
                printf(
                    "Warning: file reads-filter.pl.o[:digits:] not found in [%s/log] directory\n",
                    basename($dir->getPathname())
                );
            } else if (! file_exists($filepath)) {
                printf("Warning: file [%s] not found\n", $filepath);
            } else {

                $content = file_get_contents($filepath);
                $result  = preg_match('/Filtered (\\d+) of (\\d+) reads/', $content, $matches);

                if ($result == 0) {
                    printf("Warning: file [%s] has no statistics\n", $filepath);
                } else {
                    $nReadsTotal = $matches[2];
                }
            }

            if (is_numeric($nReadsFiltered) && is_numeric($nReadsTotal)) {
                $percentFiltered = round(1 - ($nReadsFiltered / $nReadsTotal), 2);
            }

            // create output string
            $outputStrings[$dir->getFilename()] = $humanMappingPercent . "\t" .
                $relativeToUnmappedMappingPercent . "\t" .
                $nReadsFiltered . "\t" .
                $nReadsTotal . "\t" .
            $percentFiltered;
        }

        ksort($outputStrings, SORT_STRING);
        foreach ($outputStrings as $filename => $outputString) {
            fwrite($output, $filename . "\t" . $outputString . "\n");
        }

        fclose($output);
    } else {
        echo 'Warn: cannot create result file for mapping stats', "\n";
    }

} catch (Exception $e) {
    $parser->displayError($e->getMessage());
}


class BowtieStatsFinder {


    const REFERENCE_FILE = "reference";
    const HUMAN_FILE = "human";


    public static function getBowtieStatsLogFile($projectDir, $type) {

        $logFile = null;
        if ($type == self::HUMAN_FILE) {
            $humanMappingLogFile = $projectDir . DIRECTORY_SEPARATOR . 'log' . DIRECTORY_SEPARATOR . 'human-mapping.log';
            if (file_exists($humanMappingLogFile)) {
                $logFile = new \org\sai\file\bowtie\BowtieStat($humanMappingLogFile);
            } else {
                $dirIterator = new RegexIterator(new DirectoryIterator(dirname($humanMappingLogFile)), '/bowtie.*human.*mapping\.e\d+/');
                foreach ($dirIterator as $file) {
                    $logFile = new \org\sai\file\bowtie\Bowtie2Stat(dirname($humanMappingLogFile) . DIRECTORY_SEPARATOR . $file);
                    break;
                }
            }
        } else {
            $refernceMappingLogFile = $projectDir . DIRECTORY_SEPARATOR . 'log' . DIRECTORY_SEPARATOR . 'reference-mapping.log';
            if (file_exists($refernceMappingLogFile)) {
                $logFile = new \org\sai\file\bowtie\BowtieStat($refernceMappingLogFile);
            } else {
                $dirIterator = new RegexIterator(new DirectoryIterator(dirname($refernceMappingLogFile)), '/bowtie.*reference.*mapping\.e\d+/');
                foreach ($dirIterator as $file) {
                    $logFile = new \org\sai\file\bowtie\Bowtie2Stat(dirname($refernceMappingLogFile) . DIRECTORY_SEPARATOR . $file);
                    break;
                }
            }
        }

        return $logFile;
    }
}


/**
 * Regexp file iterator/filter.
 */
class FileIterator extends FilterIterator {


    private $_regexp;


    public function __construct($dir) {

        if (! file_exists($dir)) {
            throw new Exception('Directory "' . $dir . '" not found');
        }

        parent::__construct(new DirectoryIterator($dir));
        $this->_regexp = '/^.*_\d+\.(genus|org|COG|GO|KObp)\.txt$/';
    }


    public function accept() {

        $file = $this->getInnerIterator()->current();
        return preg_match($this->_regexp, $file->getFilename());
    }


    public static function isHidden($dirName) {

        return substr($dirName, 0, 1) == '.';
    }


    public static function getFileByPattern($pattern, $dir) {

        $iterator = new RegexIterator(new DirectoryIterator($dir), $pattern);
        foreach ($iterator as $file) {
            return $file;
        }
    }
}
