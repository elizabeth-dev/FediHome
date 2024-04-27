package sh.elizabeth.fedihome.data.repository

import kotlinx.coroutines.flow.first
import sh.elizabeth.fedihome.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.fedihome.util.SupportedInstances
import javax.inject.Inject

class InternalDataRepository @Inject constructor(private val internalDataLocalDataSource: InternalDataLocalDataSource) {
	suspend fun getInstanceAndTypeAndToken(activeAccount: String): Triple<String, SupportedInstances, String> =
		activeAccount.let {
			val internalData = internalDataLocalDataSource.internalData.first()
			val instance = it.split('@')[1]
			Triple(
				instance,
				internalData.instances[instance]?.instanceType!!,
				internalData.accounts[it]?.accessToken!!
			)
		}

	suspend fun getAccountByPushAccountId(pushAccountId: String) =
		internalDataLocalDataSource.internalData.first().accounts.entries.find {
			it.value.pushData.pushAccountId == pushAccountId
		}
}