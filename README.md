Custom SMS Authenticator for Keycloak version 12.0.2
===================================================

1. Provide implementation to send SMS in com.sms.keycloak.authentication.sms.SMSImplementation class. 

2. Then go to the project directory and execute the following command.  This will build the project

   "mvn clean install"
   
3. After the project is built, go to the target folder and copy the following jar : "custom-sms-authenticator-jar-with-dependencies.jar"

4. Now go to KEYCLOAK_HOME Directory and create a folder named as "providers" and paste the jar file here. you can rename the jar whatever you want to in this directory.   
      
5. Copy the `secret-code.ftl` and `error-page.ftl` files from project directory templates folder to the `KEYCLOAK_HOME/themes/base/login` server directory.

6. Launch the keycloak server. If it's already running, restart it.

7. Login to admin console.

8. Go to the **Authentication** menu item and go to the **Flows** tab, you will be able to view the currently
   defined flows.  You cannot modify an built in flows, so, to add the Authenticator you
   have to create your own.  Click the "New" Button.
   
9: Now give an alias to your form flow e.g (SMSAuthenticationFlow or SMSAuthenticator etc) and save it.

10. Now in your flow , click on "Add Execution" button and then choose "Username Form" as your first execution step and save it.

11. Repeat step 10 and this time choose "Secret SMS Code" execution step.

12. Make sure the requirement for both execution steps is marked as required.

13. Go to the "Bindings" tab in "Authentication" menu and change the default **Browser Flow** to your custom flow 
   and click `Save`.
   
14. Now click on users menu item and create a new username

15. The username will be a phone number like this e.g (+92300XXXXXXX)

16. After creating the user, go to user role mapping and assign all the available roles to that user e.g (admin,create-realm)

17. Now Logout and login from the phone number

18. In the second screen it will ask you for security code, enter it and you're logged in.

