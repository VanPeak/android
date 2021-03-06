package cn.aws.cognitobjs;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.regions.Regions;

import cn.aws.cognitobjs.model.AuthenticationRequestModel;
import cn.aws.cognitobjs.model.AuthenticationResponseModel;

/**
 * 开发人员验证提供商
 */
public class DeveloperAuthenticationProvider extends AWSAbstractCognitoDeveloperIdentityProvider {

    public static final String TAG = "cogdev_DevAuthProvider";
    
    private static final String developerProvider = "cn.aws.cognitodev";

    private CognitoDevClient apiClient;
    private String username;
    private String password;

    public DeveloperAuthenticationProvider(String accountId, String identityPoolId, Context context, Regions region, String username, String password) {
        super(accountId, identityPoolId, region);
        // 把调用 API Gateway 相关的对象初始化出来
        AWSCredentialsProvider apiCredentialsProvider = new CognitoCachingCredentialsProvider(context, identityPoolId, region);
        ApiClientFactory factory = new ApiClientFactory().region(MainActivity.REGION.getName()).credentialsProvider(apiCredentialsProvider);
        this.apiClient = factory.build(CognitoDevClient.class);
        this.username = username;
        this.password = password;
    }

    // Return the developer provider name which you choose while setting up the
    // identity pool in the &COG; Console
    @Override
    public String getProviderName() {
        return developerProvider;
    }

    // Use the refresh method to communicate with your backend to get an
    // identityId and token.

    @Override
    public String refresh() {
        // Override the existing token
        setToken(null);

        // Get the identityId and token by making a call to your backend
        // (Call to your backend)

        // Call the update method with updated identityId and token to make sure
        // these are ready to be used from Credentials Provider.
        AuthenticationRequestModel authRequest = new AuthenticationRequestModel();
        authRequest.setUserName(username);
        authRequest.setPasswordHash(password);
        AuthenticationResponseModel authResponse = apiClient.loginPost(authRequest);
        Log.d(TAG, "refresh: " + authResponse.getUserId() + " " + authResponse.getIdentityId() + " " + authResponse.getOpenIdToken());
        identityId = authResponse.getIdentityId();
        String token = authResponse.getOpenIdToken();

        update(identityId, token);
        return token;

    }

    // If the app has a valid identityId return it, otherwise get a valid
    // identityId from your backend.

    @Override
    public String getIdentityId() {
        // Load the identityId from the cache
//        identityId = cachedIdentityId;

//        if (identityId == null) {
//            // Call to your backend
//        } else {
//            return identityId;
//        }
        AuthenticationRequestModel authRequest = new AuthenticationRequestModel();
        authRequest.setUserName(username);
        authRequest.setPasswordHash(password);
        AuthenticationResponseModel authResponse = apiClient.loginPost(authRequest);
        Log.d(TAG, "getIdentityId: " + authResponse.getUserId() + " " + authResponse.getIdentityId() + " " + authResponse.getOpenIdToken());
        identityId = authResponse.getIdentityId();
        return identityId;
    }
}