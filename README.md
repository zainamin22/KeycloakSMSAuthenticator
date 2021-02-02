Custom SMS Authenticator for Keycloak version 12.0.2
===================================================

This Custom Keycloak Authenticator provides an extra authentication flow to login the user based on phone number verification.

**Note:** Provide implementation to send SMS in com.sms.keycloak.authentication.sms.SMSImplementation class

## Standalone install
1. Then go to the project directory and execute the following command.  This will build the project

   `mvn clean install`
   
2. After the project is built, go to the target folder and copy the following jar : "custom-sms-authenticator-jar-with-dependencies.jar"

3. Now go to KEYCLOAK_HOME Directory and create a folder named as "providers" and paste the jar file here. you can rename the jar whatever you want to in this directory.   
      
4. Copy the `secret-code.ftl` and `error-page.ftl` files from project directory templates folder to the `KEYCLOAK_HOME/themes/base/login` server directory.

5. Launch the keycloak server. If it's already running, restart it.

6. Login to admin console.

7. Go to the **Authentication** menu item and go to the **Flows** tab, you will be able to view the currently
   defined flows.  You cannot modify an built in flows, so, to add the Authenticator you
   have to create your own.  Click the "New" Button.
   
8. Now give an alias to your form flow e.g (SMSAuthenticationFlow or SMSAuthenticator etc) and save it.

9. Now in your flow , click on "Add Execution" button and then choose "Username Form" as your first execution step and save it.

10. Repeat step 9 and this time choose "Secret SMS Code" execution step.

11. Make sure the requirement for both execution steps is marked as required.

12. Go to the "Bindings" tab in "Authentication" menu and change the default **Browser Flow** to your custom flow 
   and click `Save`.
   
13. Now click on users menu item and create a new username

14. The username will be a phone number like this e.g (+92300XXXXXXX)

15. After creating the user, go to user role mapping and assign all the available roles to that user e.g (admin,create-realm)

16. Now Logout and login from the phone number

17. In the second screen it will ask you for security code, enter it and you're logged in.

