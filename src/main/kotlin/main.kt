package ru.netology

class ContentNotFoundException(message: String) : RuntimeException(message)

interface Content<T> {
    val id: Int
    val authorId: Int
    val date: Long
    val comments: List<Comment>
    var isDeleted: Boolean
    var text:String
    var title: String
    fun copyWithId(newId: Int): T
    fun withNewComment(newComments: List<Comment>): T
    fun copyWithDeletedComment(newComments: List<Comment>): T
    fun copyWithNewInformation(newTitle: String, newText: String): T
    fun copyDeleted(isDeleted: Boolean): T
}

data class Comment(
    val authorId: Int,
    val commentId: Int,
    val comment: String,
    var isDeleted: Boolean = false
)
 data class Note(
    override val id: Int,
    override val authorId: Int,
    override val date: Long,
    override val comments: List<Comment>,
    override var isDeleted: Boolean = false,
    override var title: String,
    override var text: String

) : Content<Note> {
     override fun copyWithId(newId: Int): Note {
         return this.copy(id = newId)
     }

     override fun withNewComment(newComments: List<Comment>): Note {
         return copy(comments = newComments)
     }

     override fun copyWithDeletedComment(newComments: List<Comment>): Note {
         return this.copy(comments = newComments)
     }

     override fun copyWithNewInformation(newTitle: String, newText: String): Note {
         return copy(title = newTitle, text = newText)
     }
     override fun copyDeleted(isDeleted: Boolean): Note {
         return copy(isDeleted = true)
     }
}

class ContentService<T : Content<T>> {
    private val anyContent = mutableListOf<T>()
    private var nextId = 1
    private var nextCommentId = 1

    fun add(content: T): T {

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

                val updatedComments = content.comments + newComment

                val updatedContent = content.withNewComment(updatedComments)

                val index = anyContent.indexOfFirst { it.id == searchId }
                anyContent[index] = updatedContent

                return newComment
            }
        }
        throw ContentNotFoundException("Content with id $searchId not found")
    }

    fun delete(searchId: Int, id: Int): Content<T>{
            val indexOfSearchedContent = anyContent.indexOfFirst { it.id == searchId }
        if (indexOfSearchedContent == -1) throw ContentNotFoundException("Content with id $searchId not found")
        val contentToUpdate = anyContent[indexOfSearchedContent]

            val updatedContent = contentToUpdate.copyDeleted(contentToUpdate.isDeleted)


            anyContent[indexOfSearchedContent] = updatedContent
            return updatedContent
    }

    fun deleteComment(searchCommentId: Int, commentId: Int) : Content<T> {
        for ((contentIndex, content) in anyContent.withIndex()) {
            val indexOfSearchedComment = content.comments.indexOfFirst { it.commentId == searchCommentId }

                val updatedComment = content.comments[indexOfSearchedComment].copy(isDeleted = true)


                val updatedComments = content.comments.toMutableList().apply {
                    set(indexOfSearchedComment, updatedComment)
                }


                val updatedContent = content.copyWithDeletedComment(updatedComments)


                anyContent[contentIndex] = updatedContent
                return updatedContent
        }
        throw ContentNotFoundException("Content with id $searchCommentId not found")
    }

    fun editComment(searchCommentId: Int, commentId: Int, newText: String, comment: Comment) : Content<T> {
        for ((contentIndex, content) in anyContent.withIndex()) {
            val indexOfSearchedComment = content.comments.indexOfFirst { it.commentId == searchCommentId }
            val updatedComment = content.comments[indexOfSearchedComment].copy(comment = newText)

            val updatedComments = content.comments.toMutableList().apply {
                set(indexOfSearchedComment, updatedComment)
            }

            val updatedContent = content.copyWithDeletedComment(updatedComments)

            anyContent[contentIndex] = updatedContent
            return updatedContent
    }
        throw ContentNotFoundException("Content with id $searchCommentId not found")
    }

    fun edit(searchId: Int, newTitle: String, newText: String) : Content<T> {
        val indexOfSearchedContent = anyContent.indexOfFirst { it.id == searchId }
        if (indexOfSearchedContent == -1) throw ContentNotFoundException("Content with id $searchId not found")
        val contentToUpdate = anyContent[indexOfSearchedContent]
            val updatedContent = contentToUpdate.copyWithNewInformation(newTitle, newText)
        anyContent[indexOfSearchedContent] = updatedContent

            return updatedContent

    }

    fun restoreComment(searchCommentId: Int, commentId: Int) : Content<T> {
        for ((contentIndex, content) in anyContent.withIndex()) {
            val indexOfSearchedComment = content.comments.indexOfFirst { it.commentId == searchCommentId }

            val updatedComment = content.comments[indexOfSearchedComment].copy(isDeleted = false)


            val updatedComments = content.comments.toMutableList().apply {
                set(indexOfSearchedComment, updatedComment)
            }


            val updatedContent = content.copyWithDeletedComment(updatedComments)


            anyContent[contentIndex] = updatedContent
            return updatedContent
        }
        throw ContentNotFoundException("Content with id $searchCommentId not found")
    }

    fun printAll() {
        for (content in anyContent) {
            if (!content.isDeleted) {
                println("Id: ${content.id}  Note: ${content.title}")
                println("${content.text}")


                val activeComments = content.comments.filter { !it.isDeleted }

                if (activeComments.isEmpty()) {
                    println("No comments")
                } else {
                    println("Comments:")
                    // Выводим только комментарии
                    activeComments.forEach { comment ->
                        println("${comment.comment} (author: ${comment.authorId})")
                    }
                }
                println()
            }
        }
    }


}

fun main() {

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
        searchId = addedFirstNote.id,
        authorId = 2,
        text = "same to you!"
    )

    val secondtNote = Note(
        id = 0,
        authorId = 1,
        date = System.currentTimeMillis(),
        comments = emptyList<Comment>(),
        title = "Good morning!",
        text = "It's such a rainy day!",
    )
   val addedSecondNote = noteService.add(secondtNote)

    val newComment = noteService.createComment(
        searchId = addedSecondNote.id,
        authorId = 15,
        text = "Morning!"
    )

    noteService.printAll()


    noteService.deleteComment(
        searchCommentId = 1,
        commentId = 1
    )

    noteService.printAll()

    noteService.restoreComment(
        searchCommentId = 1,
        commentId = 1
    )
    noteService.printAll()

    noteService.delete(1, addedFirstNote.id)
    noteService.printAll()

    noteService.edit(2, "Good afternoon", "It's such a rainy day!")
    noteService.printAll()

}