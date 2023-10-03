package sh.elizabeth.fedihome.domain

import kotlinx.coroutines.flow.Flow
import sh.elizabeth.fedihome.data.repository.TimelineRepository
import sh.elizabeth.fedihome.model.Post
import javax.inject.Inject

class GetTimelineUseCase @Inject constructor(
	private val timelineRepository: TimelineRepository,
) {
	operator fun invoke(profileIdentifier: String): Flow<List<Post>> =
		timelineRepository.getTimeline(profileIdentifier)
}
