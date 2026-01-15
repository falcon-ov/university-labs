pipeline {
    agent {
        label 'ansible-agent'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Cloning repository with Ansible playbook...'
                checkout scm
            }
        }
        
        stage('Setup Test Server') {
            steps {
                echo 'Running Ansible playbook to configure test server...'
                dir('lab05/ansible') {
                    sh '''
                        ansible-playbook -i hosts.ini setup_test_server.yml -v
                    '''
                }
            }
        }
    }
    
    post {
        always {
            echo 'Ansible setup pipeline completed.'
        }
        success {
            echo '✓ Test server configured successfully!'
        }
        failure {
            echo '✗ Failed to configure test server.'
        }
    }
}