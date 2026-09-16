with open("app/src/main/java/com/example/ui/screens/MainScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    "TodayScreen(viewModel = viewModel)",
    """TodayScreen(viewModel = viewModel, onNavigateToMissingKm = {
                    viewModel.activateMissingKmFilter()
                    navController.navigate(Screen.History.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })"""
)

old_report_call = """                com.example.ui.screens.ReportScreen(
                    viewModel = viewModel,
                    onNavigateToHistory = {
                        navController.navigate(Screen.History.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )"""

new_report_call = """                com.example.ui.screens.ReportScreen(
                    viewModel = viewModel,
                    onNavigateToMissingKm = {
                        viewModel.activateMissingKmFilter()
                        navController.navigate(Screen.History.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )"""

content = content.replace(old_report_call, new_report_call)

with open("app/src/main/java/com/example/ui/screens/MainScreen.kt", "w") as f:
    f.write(content)
