package fr.avenirsesr.portfolio.interoperability.externalskill.application.adapter.dto;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "pathSegments", "type"})
public record ExternalSkillDTO(
    UUID id,
    String title,
    List<String> pathSegments,
    @Schema(ref = "#/components/schemas/EExternalSkillType") EExternalSkillType type) {}
