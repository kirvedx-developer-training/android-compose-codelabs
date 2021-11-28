package com.example.compose.rally

import androidx.compose.material.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.compose.rally.ui.components.RallyTopAppBar
import com.example.compose.rally.ui.theme.RallyTheme
import org.junit.Rule
import org.junit.Test
import java.util.*

class TopAppBarTest {

    // We could start the app's main activity similarly to how you would do it in the Android View
    // world:
    //@get:Rule
    //val composeTestRule = createAndroidComposeRule( RallyActivity::class.java )

    // However, with Compose, we can simplify things considerably by testing a component in
    // isolation. We can choose what Compose UI content to use in the test, with the setContent()
    // method of the ComposeTestRule class, and you can call it anywhere - only just once.
    @get:Rule
    val composeTestRule = createComposeRule()

    // TODO: Add tests

    // Example:
    //
    //@Test
    //fun myTest() {
    //    composeTestRule.setContent {
    //        Text( "You can set any Compose content" )
    //    }
    //}

    @Test
    fun rallyTopAppBarTest_tabSelectionChangesSelected() {
        //val allScreens = RallyScreen.values().toList()
        composeTestRule.setContent {
            RallyApp()
        }

        // First Let's verify the current selection (should be overview)
        composeTestRule
            .onNodeWithContentDescription( RallyScreen.Overview.name )
            .assertIsSelected()

        // Next let's click the Accounts Tab
        composeTestRule
            .onNodeWithContentDescription( RallyScreen.Accounts.name )
            .performClick()

        // Finally, let's verify that Accounts is the current selection.
        composeTestRule
            .onNodeWithContentDescription(  RallyScreen.Accounts.name )
            .assertIsSelected()

        // Now let's repeat the process for Bills
        composeTestRule
            .onNodeWithContentDescription( RallyScreen.Bills.name )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription( RallyScreen.Bills.name )
            .assertIsSelected()

        // And finally wrap back around to Overview!
        composeTestRule
            .onNodeWithContentDescription( RallyScreen.Overview.name )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription( RallyScreen.Overview.name )
            .assertIsSelected()
    }

    @Test
    fun rallyTopAppBarTest_currentTabSelected() {
        val allScreens = RallyScreen.values().toList()
        composeTestRule.setContent {
            RallyTheme {
                RallyTopAppBar(
                    allScreens = allScreens,
                    onTabSelected = {},
                    currentScreen = RallyScreen.Accounts
                )
            }
        }
        composeTestRule
            .onNodeWithContentDescription( RallyScreen.Accounts.name )
            .assertIsSelected()
    }

    @Test
    fun rallyTopAppBarTest_currentLabelExists() {
        val allScreens = RallyScreen.values().toList()
        composeTestRule.setContent {
            RallyTopAppBar(
                allScreens = allScreens,
                onTabSelected = { },
                currentScreen = RallyScreen.Accounts
            )
        }

        // Print the semantic tree to debug our test and figure out why we can't find the
        // label text:
        //composeTestRule.onRoot(
        //    useUnmergedTree = true  // Use unmerged tree to find the text for the label merged
        //).printToLog( "currentLabelExists" ) // into the selectable (see RallyTab, TopAppBar.kt)

        composeTestRule
            .onNode(
                hasText( RallyScreen.Accounts.name.uppercase( Locale.getDefault() ) ) and
                hasParent(
                    hasContentDescription( RallyScreen.Accounts.name )
                ),
                useUnmergedTree = true   // All finders have this parameter - so after debugging, we can
            )                           // enable it here, instead of with printing to the log
            .assertExists()
    }

}