package ru.netology

class PostNotFoundException(message: String) : RuntimeException(message)

interface Content<A> {
    val id: Int
    val authorId: Int
    val date: Long
    val comment: Comment
    fun copyWithId(newId: Int): A
    fun withNewComment(newComment: Comment): A
}

data class Comment(
    val authorId: Int,
    val commentId: Int,
    val comment: String
)
 data class Note(
    override val id: Int,
    override val authorId: Int,
    override val date: Long,
    override val comment: Comment,
    val title: String,
    val text: String
) : Content<Note> {
     override fun copyWithId(newId: Int): Note {
         return this.copy(id = newId)
     }

     override fun withNewComment(newComment: Comment): Note {
         return this.copy(comment = newComment)
     }
}

class ContentService<A : Content<A>> {
    private val anyContent = mutableListOf<A>()
    private var nextId = 1
    private var nextCommentId = 1

    fun add(content: A): A {

        val newContent = content.copyWithId(nextId++)
        anyContent += newContent
        return newContent
    }

    fun createComment(searchId: Int, authorId: Int, text: String): Comment {
        for (content in anyContent) {
            if (content.id == searchId) {
                val newComment = Comment(
                    authorId = authorId,
                    commentId = nextCommentId++,
                    comment = text
                )

                val updatedContent = content.withNewComment(newComment)

                val index = anyContent.indexOfFirst { it.id == searchId }
                anyContent[index] = updatedContent

                return newComment
            }
        }
        throw IllegalArgumentException("Content with id $searchId not found")
    }

}