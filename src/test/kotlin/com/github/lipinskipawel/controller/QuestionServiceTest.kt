package com.github.lipinskipawel.controller

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuestionServiceTest {

    @Test
    fun `should load UUID from the File`(@TempDir file: File) {
        val tempFile = file.resolve("test.txt")
        val randomUUID = UUID.randomUUID()
        tempFile.writeText(randomUUID.toString())
        val questionService = QuestionService(InMemoryQuestions())

        val uuid: Optional<UUID>? = questionService.loadUUID(tempFile)

        Assertions.assertThat(uuid?.get()).isEqualToComparingFieldByFieldRecursively(randomUUID)
    }
}
