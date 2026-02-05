pipeline {

  agent any

  tools {
    jdk   'jdk17'
    maven 'maven'
  }

  environment {
    MAVEN_OPTS = '-Xmx1024m'
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Unit Tests') {
      steps {
        dir('backend') {
          bat 'mvn clean verify'
        }
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
          archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
        }
      }
    }

  }

  post {
    success {
      echo 'Pipeline OK'
    }
    failure {
      echo 'Pipeline KO'
    }
  }
}
