package com.olafros.live.controller

import com.olafros.live.model.Game
import com.olafros.live.repository.GameRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/games")
class ArticleController(private val gameRepository: GameRepository) {

    @GetMapping
    fun getAllArticles(): List<Game> =
        gameRepository.findAll()


    @PostMapping
    fun createNewArticle(@Valid @RequestBody article: Game): Game =
        gameRepository.save(article)


    @GetMapping("/{id}")
    fun getArticleById(@PathVariable(value = "id") articleId: Long): ResponseEntity<Game> {
        return gameRepository.findById(articleId).map { article ->
            ResponseEntity.ok(article)
        }.orElse(ResponseEntity.notFound().build())
    }

    @PutMapping("/{id}")
    fun updateArticleById(
        @PathVariable(value = "id") articleId: Long,
        @Valid @RequestBody newArticle: Game
    ): ResponseEntity<Game> {

        return gameRepository.findById(articleId).map { existingArticle ->
            val updatedArticle: Game = existingArticle
                .copy(title = newArticle.title, time = newArticle.time)
            ResponseEntity.ok().body(gameRepository.save(updatedArticle))
        }.orElse(ResponseEntity.notFound().build())

    }

    @DeleteMapping("/{id}")
    fun deleteArticleById(@PathVariable(value = "id") articleId: Long): ResponseEntity<Void> {

        return gameRepository.findById(articleId).map { article ->
            gameRepository.delete(article)
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElse(ResponseEntity.notFound().build())

    }
}
