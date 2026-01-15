pipeline {
    agent {
        label 'php-agent'
    }
    
    stages {        
        stage('Checkout') {
            steps {
                echo 'Cloning repository...'
                checkout scm
            }
        }
        
        stage('Install Dependencies') {
            steps {
                echo 'Installing dependencies...'
                dir('lab05/php-project') {
                    sh 'composer install --no-interaction --prefer-dist'
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                echo 'Running PHPUnit tests...'
                dir('lab05/php-project') {
                    sh 'php vendor/bin/phpunit --testdox'
                }
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline completed.'
        }
        success {
            echo '✓ All tests passed successfully!'
        }
        failure {
            echo '✗ Tests failed or errors detected.'
        }
    }
}