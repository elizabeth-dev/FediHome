package sh.elizabeth.wastodon.domain

import kotlinx.coroutines.flow.Flow
import sh.elizabeth.wastodon.data.repository.TimelineRepository
import sh.elizabeth.wastodon.model.Post
import javax.inject.Inject

class GetTimelineUseCase @Inject constructor(
	private val timelineRepository: TimelineRepository,
) {
	operator fun invoke(profileIdentifier: String): Flow<List<Post>> =
		timelineRepository.getTimeline(profileIdentifier)
}
