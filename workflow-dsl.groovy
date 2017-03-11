def version

node {
	withCredentials([[$class: 'StringBinding', credentialsId: 'docker', variable: 'password']]) {
    	git url: 'https://github.com/rasanjaya85/weather-service.git'
    	Properties properties = new Properties()
        def file = readFile('gradle.properties')
	    properties.load(new StringReader(file))
	    version = "${properties.getProperty('version')}.${env.BUILD_NUMBER}"
    	sh """
      	docker login -u rasanjaya85 -p ${env.password} -e rlsubasinghe85@gmail.com
      	export GRADLE_OPTS=-Ddocker.push=true
        ./gradlew clean dockerImage
        """
  }
}

node {
	git url: 'https://github.com/rasanjaya85/marathon-deploy-script.git'
	sh """
	  ./marathon registry.cloudstaging.wso2.com:5000/weather-service:$version weather-service LOGSTASH_ENABLED=false
	"""
}
