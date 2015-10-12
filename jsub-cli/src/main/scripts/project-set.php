#!/usr/bin/php
<?php


require_once 'Console/CommandLine.php';


/*
 * Create command line parser object.
 */
$parser = new Console_CommandLine();
$parser->description = <<<DESC
Tooll for generating jsub command strings
DESC;
$parser->version = '1.0';

/*
 * Describe command line options and arguments.
 */
$parser->addCommand('create', array(
   'description' => 'jsub create command'
));
$parser->addCommand('assemble', array(
'description' => 'jsub assemble command'
));
$parser->addCommand('service', array(
    'description' => 'jsub service command'
));
$parser->addOption('pattern', array(
    'long_name'   => '--pattern',
    'description' => 'projects directory pattern',
    'action'      => 'StoreString'
));
$parser->addOption('start', array(
    'long_name'   => '--start',
    'short_name'  => '-s',
    'description' => 'start phase',
    'action'      => 'StoreString'
));
$parser->addOption('property', array(
    'long_name'   => '--property',
    'short_name'  => '-p',
    'description' => 'input property',
    'action'      => 'StoreString'
));
$parser->addOption('rproperty', array(
    'long_name'   => '--rproperty',
    'short_name'  => '-r',
    'description' => 'input regexp property',
    'action'      => 'StoreString'
));
$parser->addOption('type', array(
    'long_name'   => '--type',
    'short_name'  => '-t',
    'description' => 'jsub project type',
    'action'      => 'StoreString'
));


/*
 * Set evironment variables.
 */
$workDir     = getcwd();


try {
    $cli = $parser->parse();

    /*
     * Create iterator for traverse through picked directories. 
     */
    if ($cli->options['pattern'] === null) {
        $regexp      = null;
        $iterator = new DirectoryIterator($workDir);
    } else {
        $regexp      = '/' . $cli->options['pattern'] . '/';
        $iterator = new RegexIterator(new DirectoryIterator($workDir), $regexp);
    }

    /*
     * Loop through directories and create jsub command string.
     */
    $files = array();
    foreach ($iterator as $directory) {
        if (! $directory->isDir() || $directory->isDot()) {
            continue;
        }

        $project = ProjectData::factory($directory, $cli, $regexp);
        if ($cli->command_name == 'create') {
            echo 'jsub ' . $cli->command_name . ' -t ' . $cli->options['type'] . 
                 ' -n ' . $project->getProjectName() .
                 ' --build-dir ' . $project->getBuildDirectory() . ' --force', "\n";
        } else if (in_array($cli->command_name, array('assemble', 'service'))) {
            echo 'jsub ' . $cli->command_name . ' -t ' . $cli->options['type'] .
            ' -n ' . $project->getProjectName() .
            ' --build-dir ' . $project->getBuildDirectory() . ' --skip-test --skip-copy' .
            ' --start=' . $cli->options['start'] .
            ' --properties ' . $project->getProperties() . ' --force', "\n";
        }
    }

} catch (Exception $e) {
    $parser->displayError($e->getMessage());
}


abstract class ProjectData {

    protected $_directory;
    protected $_cwd;
    protected $_cli;

    public function factory($directory, $cli, $regex = null) {

        if ($regex === null) {
            return new SimpleProjectData($directory, $cli);
        } else {
            return new RegexpProjectData($directory, $cli, $regex);
        }
    }

    public function __construct($directory, $cli, $regex = null) {
        $this->_directory = $directory;
        $this->_cwd = getcwd();
        $this->_cli = $cli;
    }

    abstract public function getProjectName();

    public function getBuildDirectory() {
        return $this->_cwd . DIRECTORY_SEPARATOR . $this->_directory;
    }

    public function getProperties() {

        $properties = explode(',', $this->_cli->options['property']);
        foreach (explode(',', $this->_cli->options['rproperty']) as $property) {
            $pair = explode('=', $property);
            $regex = '/' . $pair[1] . '/';
            foreach (new DirectoryIterator($this->getBuildDirectory()) as $file) {
                if (preg_match($regex, $file)) {
                    $properties[] = $pair[0] . '=' . $this->getBuildDirectory() . DIRECTORY_SEPARATOR . $file;
                }
            }
        }

        foreach ($properties as & $property) {
            $property = strtr($property, array(
                '${build.dir}' => $this->getBuildDirectory(),
                '${project.name}' => $this->getProjectName()
            ));
        }

        return implode(',', $properties);
    }
}


class RegexpProjectData extends ProjectData {

    private $_regex;

    public function __construct($directory, $cli, $regex) {
        parent::__construct($directory, $cli, $regex);
        $this->_regex = $regex;
    }

    public function getProjectName() {
        preg_match($this->_regex, $this->_directory, $matches);
        return $matches[1];
    }
}


class SimpleProjectData extends ProjectData {

    public function getProjectName() {
        return $this->_directory;
    }
}