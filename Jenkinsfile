pipeline {

  agent any

  tools {
    jdk   'jdk17'
    maven 'maven'
  }

  environment {
    MAVEN_OPTS = '-Xmx1024m'
    // SonarCloud configuration
    SONAR_TOKEN = credentials('sonar-token')       // ID du credential Jenkins
    ORG = '10967'           // Remplace par ton org SonarCloud
    PROJECT_KEY = '10967_resevation_devices'               // Remplace par ton project key
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

    stage('SonarCloud Analysis') {
      steps {
        dir('backend') {
          bat """
            mvn sonar:sonar ^
            -Dsonar.projectKey=%PROJECT_KEY% ^
            -Dsonar.organization=%ORG% ^
            -Dsonar.host.url=https://sonarcloud.io ^
            -Dsonar.login=%SONAR_TOKEN%
          """
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
