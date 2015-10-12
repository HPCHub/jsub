<?php


define('COVERAGE_OUTPUT_DIR', '/data_fatso/xml_output/');


$workDirectory = getcwd();


$tag = basename($workDirectory);
$type = basename((((($workDirectory));
$analyse = substr($type, strrpos($type, '-') + 1);


$iterator = new DirectoryIterator($workDirectory);


foreach ($iterator as $directory) {
    if (! $directory->isDir() || $directory->isDot()) {
        continue;
    }

    $outputDirectory = $directory->getPathname() . DIRECTORY_SEPARATOR . 'output' . DIRECTORY_SEPARATOR;
    $files = glob($outputDirectory . 'load*.ctl');
    if (count($files) == 0) {
        printf("Warn: cannot detect .ctlfile for \"%s\"\n", $outputDirectory);
        continue;
    }

    $file = basename($files[0]);
    if (! preg_match('/load(\d+).*/', $file, $matches)) {
        printf("Warn: cannot detect mappingRunId from \"%s%s\" file name\n", $outputDirectory, $file);
        continue;
    }

    $mappingRunId = $matches[1];
    $coverageFiles = getCoverageFileNames($analyse, $tag, $outputDirectory, $mappingRunId);
    foreach ($coverageFiles as $coverageFile) {
        if (file_exists($coverageFile)) {
            continue;
        }

        $existentFile = COVERAGE_OUTPUT_DIR . basename($coverageFile);
        $result = copy($existentFile, $coverageFile);
        if (! $result) {
            printf("copy \"%s\" to \"%s\" [FAIL]\n", $existentFile, $coverageFile);
        }

        printf("copy \"%s\" to \"%s\" [OK]\n", $existentFile, $outputDirectory);
        unlink($existentFile);
    }
}


function getCoverageFileNames($analyse, $name, $outputDirectory, $mappingRunId) {

    $prefix = $outputDirectory . $name . '_' . $mappingRunId;
    if ($analyse == 'taxonomic') {
        return array(
            $prefix . '.genus.txt',
            $prefix . '.org.txt'
        );
    } elseif ($analyse == 'functional') {
        return array(
            $prefix . '.COG.txt',
            $prefix . '.GO.txt'
        );
    } else {
        return array();
    }
}