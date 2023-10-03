package sh.elizabeth.wastodon.domain

import sh.elizabeth.wastodon.data.repository.TimelineRepository
import javax.inject.Inject

class RefreshTimelineUseCase @Inject constructor(private val timelineRepository: TimelineRepository) {
	suspend operator fun invoke(profileIdentifier: String) =
		timelineRepository.fetchTimeline(profileIdentifier, profileIdentifier)

}
