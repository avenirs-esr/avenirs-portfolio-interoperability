package fr.avenirsesr.portfolio.interoperability.externalskill.xxi.domain.model;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillCategoryType;

public record Category(int id, String libelle, Category parent, EExternalSkillCategoryType type) {}
