package fr.avenirsesr.portfolio.interoperability.externalskill.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import java.util.List;

public record ExternalSkillPagedResult(List<ExternalSkill> content, PageInfo pageInfo) {}
