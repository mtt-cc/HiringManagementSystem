package it.polito.waii_24.g20.crm_old.unit.skills.services

import io.mockk.every
import io.mockk.mockk
import it.polito.waii_24.g20.crm_old.entities.Skill
import it.polito.waii_24.g20.crm_old.repositories.SkillRepository
import it.polito.waii_24.g20.crm_old.services.SkillServiceImpl
import it.polito.waii_24.g20.crm_old.unit.skills.testUtils.generateSkill
import it.polito.waii_24.g20.crm_old.unit.skills.testUtils.generateSkills
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Unit tests for the Skill services")
class SkillServiceTest {
    private val skillRepository = mockk<SkillRepository>()

    private val skillService = SkillServiceImpl(skillRepository)

    @Nested
    @DisplayName("Internal usage")
    inner class FindOrInsert {
        @Test
        @DisplayName("Insert one new skill")
        fun `insert one skill`() {
            val skill = generateSkill(1)

            every { skillRepository.findSkillBySkill(skill.skill) } answers { null }
            every { skillRepository.save(match { it.skill == skill.skill }) } answers {
                Skill().apply {
                    this.id = skill.id
                    this.skill = skill.skill
                }
            }

            val result = skillService.findOrInsert(setOf(skill.skill))
            println(result.joinToString(", "))

            assertEquals(1, result.size, "The size of the result should be 1")
            assertEquals(1, result.first().id, "The id of the result should be 1")
            assertEquals(skill.skill, result.first().skill, "The skill of the result should be the same as the one inserted")
        }

        @Test
        @DisplayName("Insert multiple new skills")
        fun `insert multiple skills`() {
            val skills = generateSkills(3)

            every { skillRepository.findSkillBySkill(any()) } answers { null }

            var counter = 0L
            every { skillRepository.save(any<Skill>()) } answers {
                val skill = firstArg<Skill>()
                Skill().apply {
                    this.id = ++counter
                    this.skill = skill.skill
                }
            }

            val result = skillService.findOrInsert(skills.map { it.skill }.toSet())
            assertEquals(3, result.size, "The size of the result should be 3")
            result.zip(skills).forEach { (resultSkill, skill) ->
                assertEquals(skill.id, resultSkill.id, "The id of the result should be the same as the one inserted")
                assertEquals(skill.skill, resultSkill.skill, "The skill of the result should be the same as the one inserted")
            }
        }

        @Test
        @DisplayName("Insert one skill that already exists")
        fun `insert one skill that already exists`() {
            val skill = generateSkill(1)

            every { skillRepository.findSkillBySkill(skill.skill) } answers { skill }

            val result = skillService.findOrInsert(setOf(skill.skill))

            assertEquals(1, result.size, "The size of the result should be 1")
            assertEquals(skill.id, result.first().id, "The id of the result should be the same as the one inserted")
            assertEquals(skill.skill, result.first().skill, "The skill of the result should be the same as the one inserted")
        }

        @Test
        @DisplayName("Insert multiple skills where some already exist")
        fun `insert multiple skills where some already exist`() {
            val skills = generateSkills(5)

            every { skillRepository.findSkillBySkill(any()) } answers {
                val s = skills.find {
                    it.skill == firstArg()
                }
                if (s!!.id!! % 2 == 0L) s else null
            }

            var ctr = 0L
            every { skillRepository.save(any<Skill>()) } answers {
                val skill = firstArg<Skill>()
                Skill().apply {
                    this.id = (++ctr)*2-1
                    this.skill = skill.skill
                }
            }

            val result = skillService.findOrInsert(skills.map { it.skill }.toSet())

            assertEquals(5, result.size, "The size of the result should be 5")
            result.zip(skills).forEach { (resultSkill, skill) ->
                assertEquals(resultSkill.id, skill.id, "The id of the result should be the same as the one inserted")
                assertEquals(resultSkill.skill, skill.skill, "The skill of the result should be the same as the one inserted")
            }

        }

        @Test
        @DisplayName("Insert multiple skills where all already exist")
        fun `insert multiple skills where all already exist`() {
            val skills = generateSkills(3)

            every { skillRepository.findSkillBySkill(any()) } answers { skills.find { it.skill == firstArg() } }

            val result = skillService.findOrInsert(skills.map { it.skill }.toSet())
            assertEquals(3, result.size, "The size of the result should be 3")
            result.zip(skills).forEach { (resultSkill, skill) ->
                assertEquals(skill.id, resultSkill.id, "The id of the result should be the same as the one inserted")
                assertEquals(skill.skill, resultSkill.skill, "The skill of the result should be the same as the one inserted")
            }
        }
    }
}