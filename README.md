# spring-cloud-demo

Sample projects to demo Spring Cloud Native applications with Spring Boot.

## Notes
All projects set the `logging.file` property in the property file like this:
```
logging.file=/${user.home}/log-dir/${spring.application.name}-${server.port}.log
```
This means that if you want it to work on your environment, you must have a directory named `log-dir` in your $HOME directory, or change the value of the `logging.file` property.

## Running with curl

Get a token using the auth-service:
```
curl -v -X POST -u myclient:mysecret http://localhost:9191/oauth/token -H "Accept: application/json" -d "password=password&username=jifa&grant_type=password&scope=openid&client_secret=mysecret&client_id=myclient"
{"access_token":"232a926f-7065-4b04-bc24-3daad0c1f076","token_type":"bearer","refresh_token":"85eeff13-2768-47c7-b826-503ece76a726","expires_in":43199,"scope":"openid"}
```

Check the user using the auth-service:
```
curl -v http://localhost:9191/user -H "Authorization: Bearer <access_token>"
{"name":"jifa","authorities":"ROLE_ADMIN:ROLE_USER"}
```

Use the greeting-client with the token:
```
curl -v http://localhost:8080/api/greet -H "Authorization: Bearer <access_token>"
{"greeting":"Hello World!"}
```

Revoke token, meaning the user has to get a new token to be able to use the greeting-client again:
```
curl -v http://localhost:9191/oauth/revoke-token -H "Authorization: Bearer <access_token>"
{"greeting":"Hello World!"}
```

If you configure https:
```
keytool -genkey -alias greetings-client -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650
```

Use https in the curl call. Use -k to skip verification of the certificate, since we have a self-signed certificate.
```
curl -v https://localhost:8080/api/greet -H "Authorization: Bearer <access_token>" -k
{"greeting":"Hello World!"}
```
