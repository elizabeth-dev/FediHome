package sh.elizabeth.fedihome.model

import org.junit.Test
import sh.elizabeth.fedihome.mock.defaultPost
import kotlin.math.pow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun wrapPosts(depth: Int, generateUuid: Boolean = false): Post =
	if (depth == 0) defaultPost.copy(quote = null, boostedPost = null).let {
		if (generateUuid) it.copy(
			id = Uuid.generateV7().toString()
		)
		else it
	}
	else defaultPost.copy(quote = wrapPosts(depth - 1, generateUuid).let {
		if (generateUuid) it.copy(
			id = Uuid.generateV7().toString()
		)
		else it
	}, boostedPost = wrapPosts(depth - 1, generateUuid).let {
		if (generateUuid) it.copy(
			id = Uuid.generateV7().toString()
		)
		else it
	})


class PostTest {
	@Test
	fun unwrapPosts_SinglePost() {
		val post = defaultPost.copy(boostedPost = null, quote = null)

		assert(post.unwrapPosts().size == 1)
	}

	@OptIn(ExperimentalUuidApi::class)
	@Test
	fun unwrapPosts_QuoteAndBoost() {
		val post = defaultPost.copy(
			boostedPost = defaultPost.copy(
				id = Uuid.generateV7().toString(), quote = null, boostedPost = null
			), quote = defaultPost.copy(
				id = Uuid.generateV7().toString(), quote = null, boostedPost = null
			)
		)

		assert(post.unwrapPosts().size == 3)
	}

	@Test
	fun unwrapPosts_Deduplicate() {
		val post = wrapPosts(2)


		assert(post.unwrapPosts().size == 1)
	}

	@Test
	fun unwrapPosts_Multilayer() {
		val depth = 5
		val post = wrapPosts(depth = depth, generateUuid = true)

		assert(post.unwrapPosts().size == 2.0.pow(depth + 1).toInt() - 1)
	}
}