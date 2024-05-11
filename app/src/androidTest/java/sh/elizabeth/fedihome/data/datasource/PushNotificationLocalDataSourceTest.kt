package sh.elizabeth.fedihome.data.datasource

import androidx.work.Data
import org.junit.Test
import sh.elizabeth.fedihome.data.datasource.model.InternalDataAccount
import sh.elizabeth.fedihome.data.datasource.model.InternalDataAccount_PushData

class PushNotificationLocalDataSourceTest {
	private val pushNotificationLocalDataSource =
		PushNotificationLocalDataSource()

	@Test
	fun handleKeyNotification() {
		val account = InternalDataAccount(
			accessToken = "accessToken",
			pushData = InternalDataAccount_PushData(
				pushServerKey = "BP4z9KsN6nGRTbVYI_c7VJSPQTBtkgcy27mlmlMoZIIgDll6e3vCYLocInmYWAmS6TlzAC8wEqKK6PBru3jl7A8",
				pushAccountId = "pushAccountId",
				pushEndpoint = "pushEndpoint",
				pushPublicKey = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEJXGyvs3942BVGq8e0PTNNmwRzr5VX4m8t7GGpTM5FzFo7OLr4BhZe9MEebhuPI-OztV3ylkYfpJGmQ22ggCLDg==",
				pushPrivateKey = "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgq1dXpw3UpT5VOmu_cf_v6ih07Aems3njxI-JWgLcM96hRANCAAQlcbK-zf3jYFUarx7Q9M02bBHOvlVfiby3sYalMzkXMWjs4uvgGFl70wR5uG48j47O1XfKWRh-kkaZDbaCAIsO",
				pushAuthSecret = "BTBZMqHH6r4Tts7J_aSIgg"
			)
		)

		val messageData = Data.Builder().putString(
			"p",
			"DGv6ra1nlYgDCS1FRnbzlwAAEABBBP4z9KsN6nGRTbVYI_c7VJSPQTBtkgcy27mlmlMoZIIgDll6e3vCYLocInmYWAmS6TlzAC8wEqKK6PBru3jl7A_yl95bQpu6cVPTpK4Mqgkf1CXztLVBSt2Ks3oZwbuwXPXLWyouBWLVWGNWQexSgSxsj_Qulcy4a-fN"
		).build()

		pushNotificationLocalDataSource.handleKeyNotification(
			accountId = "accountId",
			instance = "instance",
			account = account,
			messageData = messageData
		)
	}
}