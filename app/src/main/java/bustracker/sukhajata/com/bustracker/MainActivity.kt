package bustracker.sukhajata.com.bustracker

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.View

import bustracker.sukhajata.com.bustracker.JourneyFragment.OnJourneyFragmentInteractionListener

//import com.amazonaws.Response
import com.amazonaws.amplify.generated.graphql.ListJourneysQuery
import com.amazonaws.mobile.client.*
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import kotlinx.android.synthetic.main.activity_main.*
import com.apollographql.apollo.exception.ApolloException
import javax.annotation.Nonnull
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import bustracker.sukhajata.com.bustracker.model.Journey
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers


class MainActivity : AppCompatActivity(), OnJourneyFragmentInteractionListener {

    private lateinit var mAWSAppSyncClient: AWSAppSyncClient

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_progress.visibility = View.VISIBLE

        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(AWSConfiguration(getApplicationContext()))
                .build()

        AWSMobileClient.getInstance().initialize(applicationContext, LoginCallback(this))

        observeLoginStatus()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    fun getClient() : AWSAppSyncClient {
        return mAWSAppSyncClient;
    }

    private fun observeLoginStatus() {
        AWSMobileClient.getInstance().addUserStateListener { userStateDetails ->
            when (userStateDetails.getUserState()) {
                UserState.SIGNED_IN -> {
                    Log.i("userState", userStateDetails.userState.toString())
                    getJourneys()
                }
                UserState.SIGNED_OUT, UserState.SIGNED_OUT_USER_POOLS_TOKENS_INVALID, UserState.SIGNED_OUT_FEDERATED_TOKENS_INVALID -> {
                    Log.i("userState", userStateDetails.userState.toString())
                    AWSMobileClient.getInstance().showSignIn(
                            this@MainActivity,
                            SignInUIOptions.builder()
                                    .nextActivity(MainActivity::class.java)
                                    .build(),
                            LoginCallback(this@MainActivity))
                }
                else -> Log.i("userState", "unsupported")
            }
        }
    }

    private val journeysCallback = object : GraphQLCall.Callback<ListJourneysQuery.Data>() {
        override fun onResponse(response: Response<ListJourneysQuery.Data>) {
            Log.i("Results", response.data()?.listJourneys()?.items().toString())
            val journeys = response.data()?.listJourneys()?.items()
            if (journeys != null) {
                for (item in journeys) {
                    val journey = Journey(item.id(), item.from(), item.to());
                    Journey.ITEMS.add(journey)
                }
                val fragment = JourneyFragment()
                supportFragmentManager.inTransaction {
                    add(R.layout.fragment_journey_list, fragment)
                }
            }
            val fragment = JourneyFragment()

        }

        override fun onFailure(e: ApolloException) {
            Log.e("ERROR", e.toString())
        }
    }

    fun getJourneys() {
        mAWSAppSyncClient.query(ListJourneysQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(journeysCallback)
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    override fun onJourneyFragmentInteraction(journey: Journey?) {

    }

    class LoginCallback : Callback<UserStateDetails> {
        private lateinit var mActivity : MainActivity

        constructor(activity : MainActivity) {
            mActivity = activity
        }

        override fun onResult(userStateDetails: UserStateDetails) {
            Log.i("INIT", "onResult: " + userStateDetails.userState)
            when (userStateDetails.getUserState()) {
                UserState.SIGNED_IN -> {
                    Log.i("userState", userStateDetails.userState.toString())
                    mActivity.getJourneys()
                }
                UserState.SIGNED_OUT, UserState.SIGNED_OUT_USER_POOLS_TOKENS_INVALID, UserState.SIGNED_OUT_FEDERATED_TOKENS_INVALID -> {
                    Log.i("userState", userStateDetails.userState.toString())
                    AWSMobileClient.getInstance().showSignIn(
                            mActivity,
                            SignInUIOptions.builder()
                                    .nextActivity(MainActivity::class.java)
                                    .build(),
                            LoginCallback(mActivity))
                }
                else -> Log.i("userState", "unsupported")
            }
        }

        override fun onError(e: Exception) {
            Log.e("INIT", "Initialization error.", e)
        }
    }

}
