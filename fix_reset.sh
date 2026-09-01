sed -i '/viewModel.resetAllData {/,/}/c \
                        coroutineScope.launch {\n                            viewModel.resetAllData()\n                            Toast.makeText(context, "Đã xóa toàn bộ dữ liệu", Toast.LENGTH_LONG).show()\n                        }' app/src/main/java/com/example/ui/screens/OtherScreen.kt
