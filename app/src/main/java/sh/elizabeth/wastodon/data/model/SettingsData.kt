package sh.elizabeth.wastodon.data.model

data class SettingsData(
	val appSecrets: Map<String, String>,
	val lastLoginInstance: String,
	val accessTokens: Map<String, String>,
	val activeAccount: String,
)
