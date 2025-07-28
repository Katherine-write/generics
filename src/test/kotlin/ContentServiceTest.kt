import junit.framework.TestCase.assertEquals
import org.junit.Before



import ru.netology.Comment
import ru.netology.ContentNotFoundException
import ru.netology.ContentService
import ru.netology.Note
import kotlin.test.Test

class ContentServiceTest {

    @Test(expected = ContentNotFoundException::class)
    fun shouldThrow() {

        val noteService = ContentService<Note>()

        val firstNote = Note(
            id = 0,
            authorId = 1,
            date = System.currentTimeMillis(),
            comments = emptyList<Comment>(),
            title = "Hello, everyone!",
            text = "It's such a good day!",
        )

        val addedFirstNote = noteService.add(firstNote)

        val comment = noteService.createComment(
            searchId = 15,
            authorId = 2,
            text = "same to you!"
        )


    }

    @org.junit.Test
    fun add() {
        val noteService = ContentService<Note>()

        val firstNote = Note(
            id = 0,
            authorId = 1,
            date = System.currentTimeMillis(),
            comments = emptyList<Comment>(),
            title = "Hello, everyone!",
            text = "It's such a good day!",
        )

        val result = noteService.add(firstNote)

        assertEquals(1, result.id)
    }

    @Test
    fun successfulDelete() {

        val noteService = ContentService<Note>()

        val firstNote = Note(
            id = 0,
            authorId = 1,
            date = System.currentTimeMillis(),
            comments = emptyList<Comment>(),
            title = "Hello, everyone!",
            text = "It's such a good day!",
        )

        val addedFirstNote = noteService.add(firstNote)

        val result = noteService.delete(1, addedFirstNote.id)

        val expected = addedFirstNote.copy(isDeleted = true)

        assertEquals(expected, result)
    }

    @Test(expected = ContentNotFoundException::class)
    fun failedDelete() {

        val noteService = ContentService<Note>()

        val firstNote = Note(
            id = 0,
            authorId = 1,
            date = System.currentTimeMillis(),
            comments = emptyList<Comment>(),
            title = "Hello, everyone!",
            text = "It's such a good day!",
        )

        val addedFirstNote = noteService.add(firstNote)

        noteService.delete(12, addedFirstNote.id)

    }

    @Test
    fun successfulEdit() {

        val noteService = ContentService<Note>()

        val firstNote = Note(
            id = 0,
            authorId = 1,
            date = System.currentTimeMillis(),
            comments = emptyList<Comment>(),
            title = "Hello, everyone!",
            text = "It's such a good day!",
        )

        val addedFirstNote = noteService.add(firstNote)

        val result = noteService.edit(1, "Good afternoon", "It's such a rainy day!")

        val expected = addedFirstNote.copy(title = "Good afternoon", text = "It's such a rainy day!")
        assertEquals(expected, result)
    }

    @Test
    fun successfulDeleteComment() {
        val noteService = ContentService<Note>()

        val firstNote = Note(
            id = 0,
            authorId = 1,
            date = System.currentTimeMillis(),
            comments = emptyList<Comment>(),
            title = "Hello, everyone!",
            text = "It's such a good day!",
        )

        val addedFirstNote = noteService.add(firstNote)
        val newComment = noteService.createComment(
            searchId = addedFirstNote.id,
            authorId = 15,
            text = "Morning!"
        )

        val result =  noteService.deleteComment(
            searchCommentId = 1,
            commentId = 1
        )

        val expectedComment = Comment(
            authorId = 15,
            commentId = 1,
            comment = "Morning!",
            isDeleted = true
        )

        val expected = addedFirstNote.copy(
            comments = listOf(expectedComment)
        )

        assertEquals(expected, result)
    }

}