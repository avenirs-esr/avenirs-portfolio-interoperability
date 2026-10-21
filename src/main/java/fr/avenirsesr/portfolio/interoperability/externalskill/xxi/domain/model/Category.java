package fr.avenirsesr.portfolio.interoperability.externalskill.xxi.domain.model;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillKind;

public record Category(int id, String libelle, Category parent, ESkillKind type) {}
