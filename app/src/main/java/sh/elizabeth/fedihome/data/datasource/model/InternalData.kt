package sh.elizabeth.fedihome.data.datasource.model

import sh.elizabeth.fedihome.util.SupportedInstances

data class InternalDataAccount_PushData(
	val pushPublicKey: String?,
	val pushPrivateKey: String?,
	val pushAuthKey: String?,
	val pushAccountId: String?,
)

data class InternalDataAccount(
	val accessToken: String,
	val pushData: InternalDataAccount_PushData,
)

data class InternalDataInstance(
	val instanceType: SupportedInstances,
	val appId: String?,
	val appSecret: String?,
)

data class InternalDataValues(
	val lastLoginInstance: String,
	val activeAccount: String,
	val accounts: Map<String, InternalDataAccount>,
	val instances: Map<String, InternalDataInstance>,
	val fcmDeviceToken: String?,
)