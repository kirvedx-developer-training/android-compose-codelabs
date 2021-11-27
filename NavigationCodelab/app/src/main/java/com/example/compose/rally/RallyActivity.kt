/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.rally

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.rally.data.UserData
import com.example.compose.rally.ui.accounts.AccountsBody
import com.example.compose.rally.ui.accounts.SingleAccountBody
import com.example.compose.rally.ui.bills.BillsBody
import com.example.compose.rally.ui.components.RallyTabRow
import com.example.compose.rally.ui.overview.OverviewBody
import com.example.compose.rally.ui.theme.RallyTheme

/**
 * This Activity recreates part of the Rally Material Study from
 * https://material.io/design/material-studies/rally.html
 */
class RallyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RallyApp()
        }
    }
}

@Composable
fun RallyApp() {
    RallyTheme {
        val allScreens = RallyScreen.values().toList()
        val navController = rememberNavController()
        //var currentScreen by rememberSaveable { mutableStateOf(RallyScreen.Overview) }
        val backstackEntry = navController.currentBackStackEntryAsState()
        val currentScreen = RallyScreen.fromRoute(
            backstackEntry.value?.destination?.route
        )
        Scaffold(
            topBar = {
                RallyTabRow(
                    allScreens = allScreens,
                    //onTabSelected = { screen -> currentScreen = screen },
                    onTabSelected = { screen -> navController.navigate( screen.name ) },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            // TODO: 1-1 Use this:
            RallyNavHost(
                navController = navController,
                modifier = Modifier.padding( innerPadding )
            )

            /* TODO: 1-2 To remove this:
            val accountsName = RallyScreen.Accounts.name
            NavHost(
                navController = navController,
                startDestination = RallyScreen.Overview.name,
                modifier = Modifier.padding( innerPadding )
            ) {
                // Each of these top level composable are NavGraph extension methods
                // that allow us to specify a route
                composable( RallyScreen.Overview.name ) {
                    //Text( text = RallyScreen.Overview.name )
                    OverviewBody(
                        onClickSeeAllAccounts = { navController.navigate( RallyScreen.Accounts.name ) },
                        onClickSeeAllBills = { navController.navigate( RallyScreen.Bills.name ) },
                        onAccountClick = {
                            navController.navigate( "${RallyScreen.Accounts.name}/$it" )
                        }
                    )
                }
                composable( RallyScreen.Accounts.name ) {
                    //Text( text = RallyScreen.Accounts.name )
                    AccountsBody(
                        accounts = UserData.accounts
                    ) {
                        // onAccountClick is the last parameter, and so may be provided in a
                        // trailing lambda
                        navController.navigate( "${RallyScreen.Accounts.name}/$it" )
                    }
                }
                composable( RallyScreen.Bills.name ) {
                    // Text( text = RallyScreen.Bills.name )
                    BillsBody(
                        bills = UserData.bills
                    )
                }
                // Here we specify a route that accepts arguments
                //
                // You have to store RallyScreen.Accounts.name
                // in a variable - or an exception is thrown
                // when you try to navigate to a single account
                // with an error that the path doesn't exist!
                composable(
                    route = "$accountsName/{name}",
                    arguments = listOf(
                        navArgument( "name" ) {
                            // Make argument type safe
                            type = NavType.StringType
                        }
                    ),
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = "rally://$accountsName/{name}"
                        }
                    )
                ) {
                    entry -> // Look up "name" in NavBackStackEntry's arguments
                    // And how to use the arguments
                    val accountName = entry.arguments?.getString( "name" )
                    // Find first name match in UserData
                    val account = UserData.getAccount( accountName )
                    // Pass account to SingleAccountBOdy (instead of AccountsBody)
                    SingleAccountBody( account = account )
                }
            }*/

            /*Box(Modifier.padding(innerPadding)) {
                currentScreen.content(
                    onScreenChange = { screen ->
                        currentScreen = RallyScreen.valueOf(screen)
                    }
                )
            }*/
        }
    }
}

@Composable
fun RallyNavHost (
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = RallyScreen.Overview.name,
        modifier = modifier
    ) {
        // NOTE: RuntimeException Warning:
        // We have to store the Accounts screen name (from RallyScreen enum) in a variable
        // because we cannot access data members from a route string at runtime - an error is
        // thrown.
        val accountsName = RallyScreen.Accounts.name
        composable( RallyScreen.Overview.name ) {
            OverviewBody(
                onClickSeeAllAccounts = { navController.navigate( RallyScreen.Accounts.name ) },
                onClickSeeAllBills = { navController.navigate( RallyScreen.Bills.name ) },
                onAccountClick = {
                    //navController.navigate( "${RallyScreen.Accounts.name}/$it" )
                    // Let's provide a helper method instead, for our extracted NavHost:
                    navigateToSingleAccount(
                        navController = navController,
                        accountName = it
                    )
                }
            )
        }
        composable( RallyScreen.Accounts.name ) {
            AccountsBody(
                accounts = UserData.accounts
            ) {
                // onAccountClick is the last parameter, so it can be provided in a trailing lambda
                // navController.navigate( "${RallyScreen.Accounts.name}/$it" )
                // Let's again use the helper method for our extracted NavHost
                navigateToSingleAccount(
                    navController = navController,
                    accountName = it
                )
            }
        }
        composable( RallyScreen.Bills.name ) {
            BillsBody(
                bills = UserData.bills
            )
        }
        // Here we specify a route that accepts arguments
        composable(
            //route = "$RallyScreen.Accounts.name/{name}", // See RuntimeException above
            route = "$accountsName/{name}",
            arguments = listOf(
                navArgument( "name" ) {
                    // Make argument type safe
                    type = NavType.StringType
                }
            ),  // Test deeplinks after installing latest application version with:
                // ~/Android/Sdk/platform-tools/adb shell am start \
                // -d "rally://accounts/Checking" \
                // -a android.intent.action.VIEW
            deepLinks = listOf( navDeepLink {
                    //uriPattern = "$RallyScreen.Accounts.name/{name}", // See RuntimeException above
                    uriPattern = "rally://$accountsName/{name}"
                }
            )
        ) {
            entry -> // Look up "name" in NavBackStackEntry's arguments
            // And how to use the arguments
            val accountName = entry.arguments?.getString( "name" )
            // Find first name match in UserData
            val account = UserData.getAccount( accountName )
            // Pass account to SingleAccountBOdy (instead of AccountsBody)
            SingleAccountBody( account = account )
        }
    }
}

private fun navigateToSingleAccount(
    navController: NavHostController,
    accountName: String
) {
    navController.navigate( "${RallyScreen.Accounts.name}/$accountName" )
}
