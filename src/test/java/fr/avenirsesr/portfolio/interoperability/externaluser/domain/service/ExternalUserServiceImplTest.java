package fr.avenirsesr.portfolio.interoperability.externaluser.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.interoperability.externaluser.domain.port.output.repository.ExternalUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExternalUserServiceImplTest {

  private static final String EPPN = "lucas.tessier@university.com";
  private static final String FIRST_NAME = "Lucas";
  private static final String LAST_NAME = "Tessier";
  private static final String EMAIL = "lucas.tessier@university.com";
  private static final EUserCategory CATEGORY = EUserCategory.STUDENT;
  private static final String EXTERNAL_ID = "PEG-0001";
  private static final EExternalSource SOURCE = EExternalSource.PEGASE;

  @Mock private ExternalUserRepository externalUserRepository;

  @InjectMocks private ExternalUserServiceImpl service;

  @Nested
  class GivenExternalUserService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an external user service");
    }

    @Nested
    class WhenImportingExternalUserWithStatus {

      private ExternalUser result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("importing an external user with explicit status");

        result =
            service.importExternalUser(
                EPPN,
                FIRST_NAME,
                LAST_NAME,
                EMAIL,
                CATEGORY,
                EXTERNAL_ID,
                SOURCE,
                EUserStatus.ACTIVE);
      }

      @Test
      void thenItShouldCreateExternalUser() {
        BddLogger.then("it should create external user");

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        assertEquals(EPPN, result.getEppn());
        assertEquals(FIRST_NAME, result.getFirstName());
        assertEquals(LAST_NAME, result.getLastName());
        assertEquals(EMAIL, result.getEmail());
        assertEquals(CATEGORY, result.getCategory());
        assertEquals(EXTERNAL_ID, result.getExternalId());
        assertEquals(SOURCE, result.getSource());
        assertEquals(EUserStatus.ACTIVE, result.getStatus());
      }

      @Test
      void thenItShouldSaveExternalUser() {
        BddLogger.then("it should save external user");

        ArgumentCaptor<ExternalUser> captor = ArgumentCaptor.forClass(ExternalUser.class);

        verify(externalUserRepository).save(captor.capture());
        verifyNoMoreInteractions(externalUserRepository);

        ExternalUser savedExternalUser = captor.getValue();

        assertEquals(result, savedExternalUser);
        assertEquals(EPPN, savedExternalUser.getEppn());
        assertEquals(FIRST_NAME, savedExternalUser.getFirstName());
        assertEquals(LAST_NAME, savedExternalUser.getLastName());
        assertEquals(EMAIL, savedExternalUser.getEmail());
        assertEquals(CATEGORY, savedExternalUser.getCategory());
        assertEquals(EXTERNAL_ID, savedExternalUser.getExternalId());
        assertEquals(SOURCE, savedExternalUser.getSource());
        assertEquals(EUserStatus.ACTIVE, savedExternalUser.getStatus());
      }
    }

    @Nested
    class WhenImportingExternalUserWithoutStatus {

      private ExternalUser result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("importing an external user without status");

        result =
            service.importExternalUser(
                EPPN, FIRST_NAME, LAST_NAME, EMAIL, CATEGORY, EXTERNAL_ID, SOURCE, null);
      }

      @Test
      void thenItShouldDefaultStatusToActive() {
        BddLogger.then("it should default status to active");

        assertNotNull(result);
        assertEquals(EUserStatus.ACTIVE, result.getStatus());

        ArgumentCaptor<ExternalUser> captor = ArgumentCaptor.forClass(ExternalUser.class);

        verify(externalUserRepository).save(captor.capture());
        verifyNoMoreInteractions(externalUserRepository);

        assertEquals(EUserStatus.ACTIVE, captor.getValue().getStatus());
      }
    }

    @Nested
    class WhenImportingInactiveExternalUser {

      private ExternalUser result;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("importing an inactive external user");

        result =
            service.importExternalUser(
                EPPN,
                FIRST_NAME,
                LAST_NAME,
                EMAIL,
                CATEGORY,
                EXTERNAL_ID,
                SOURCE,
                EUserStatus.INACTIVE);
      }

      @Test
      void thenItShouldKeepInactiveStatus() {
        BddLogger.then("it should keep inactive status");

        assertEquals(EUserStatus.INACTIVE, result.getStatus());

        ArgumentCaptor<ExternalUser> captor = ArgumentCaptor.forClass(ExternalUser.class);

        verify(externalUserRepository).save(captor.capture());
        verifyNoMoreInteractions(externalUserRepository);

        assertEquals(EUserStatus.INACTIVE, captor.getValue().getStatus());
      }
    }

    @Nested
    class WhenRepositoryFails {

      private RuntimeException exception;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("repository fails while saving external user");

        exception = new RuntimeException("Unable to save external user");

        doThrow(exception).when(externalUserRepository).save(any(ExternalUser.class));
      }

      @Test
      void thenItShouldPropagateException() {
        BddLogger.then("it should propagate exception");

        RuntimeException result =
            assertThrows(
                RuntimeException.class,
                () ->
                    service.importExternalUser(
                        EPPN,
                        FIRST_NAME,
                        LAST_NAME,
                        EMAIL,
                        CATEGORY,
                        EXTERNAL_ID,
                        SOURCE,
                        EUserStatus.ACTIVE));

        assertEquals(exception, result);

        verify(externalUserRepository).save(any(ExternalUser.class));
        verifyNoMoreInteractions(externalUserRepository);
      }
    }
  }
}
