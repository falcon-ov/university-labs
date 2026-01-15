pipeline {
    agent {
        label 'ansible-agent'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Cloning PHP project repository...'
                checkout scm
            }
        }
        
        stage('Prepare Deployment') {
            steps {
                echo 'Preparing project files for deployment...'
                dir('lab05/php-project') {
                    sh 'composer install --no-dev --no-interaction --prefer-dist --optimize-autoloader'
                }
            }
        }
        
        stage('Deploy to Test Server') {
            steps {
                echo 'Deploying PHP project to test server...'
                dir('lab05') {
                    sh '''
                        # Copy project files to test server
                        scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null \
                            -i /home/jenkins/.ssh/ansible/id_rsa \
                            -r php-project/src/* ansible@test-server:/var/www/html/
                        
                        # Create a simple index.php for testing
                        ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null \
                            -i /home/jenkins/.ssh/ansible/id_rsa \
                            ansible@test-server "echo '<?php require_once \"/var/www/html/Calculator.php\"; use App\\\\Calculator; \\$calc = new Calculator(); echo \"<h1>Calculator Test</h1>\"; echo \"<p>5 + 3 = \" . \\$calc->add(5, 3) . \"</p>\"; echo \"<p>10 - 4 = \" . \\$calc->subtract(10, 4) . \"</p>\"; echo \"<p>6 * 7 = \" . \\$calc->multiply(6, 7) . \"</p>\"; echo \"<p>20 / 4 = \" . \\$calc->divide(20, 4) . \"</p>\"; phpinfo();' | sudo tee /var/www/html/index.php"
                        
                        # Set proper permissions
                        ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null \
                            -i /home/jenkins/.ssh/ansible/id_rsa \
                            ansible@test-server "sudo chown -R www-data:www-data /var/www/html && sudo chmod -R 755 /var/www/html"
                    '''
                }
            }
        }
        
        stage('Verify Deployment') {
            steps {
                echo 'Verifying deployment...'
                sh '''
                    sleep 5
                    curl -s http://test-server/ | grep -q "Calculator" && echo "✓ Deployment verified!" || echo "✗ Deployment verification failed!"
                '''
            }
        }
    }
    
    post {
        always {
            echo 'Deployment pipeline completed.'
        }
        success {
            echo '✓ PHP project deployed successfully!'
            echo 'Access the application at: http://localhost:8081'
        }
        failure {
            echo '✗ Deployment failed.'
        }
    }
}
