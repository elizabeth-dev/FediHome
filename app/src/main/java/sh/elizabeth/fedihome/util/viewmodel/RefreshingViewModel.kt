package sh.elizabeth.fedihome.util.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import sh.elizabeth.fedihome.data.repository.AuthRepository

interface RefreshingViewModel {
	val authRepository: AuthRepository
	val coroutineHandlingScope: CoroutineScope

	fun refreshOnAccountChange(activeAccount: String)

	fun startRefreshOnAccountChange() =
		coroutineHandlingScope.launch {
			authRepository.activeAccount.filter {
				it.isNotBlank()
			}.distinctUntilChanged().collect {
				refreshOnAccountChange(it)
			}
		}

}