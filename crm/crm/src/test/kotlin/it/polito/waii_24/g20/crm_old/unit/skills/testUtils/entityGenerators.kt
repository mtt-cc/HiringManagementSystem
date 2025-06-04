package it.polito.waii_24.g20.crm_old.unit.skills.testUtils

import it.polito.waii_24.g20.crm_old.entities.Skill

fun generateSkill(seed: Long): Skill {
    return Skill().apply {
        this.id = seed
        this.skill = "skill.$seed"
    }
}

fun generateSkills(n: Int): Set<Skill> {
    return (1..n).map { generateSkill(it.toLong()) }.toSet()
}