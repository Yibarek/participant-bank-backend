package org.multimedia.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.multimedia.dto.ParticipantBankDto;
import org.multimedia.model.ParticipantBank;
import org.multimedia.repository.ParticipantBankRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantBankService {

    private final ParticipantBankRepository participantBankRepository;
    private final org.multimedia.util.FileStorageUtil fileStorageUtil;

    @Transactional
    public ParticipantBank createBank(ParticipantBankDto dto) {
        log.info("Creating participant bank: {}", dto.getName());

        if (participantBankRepository.existsByName(dto.getName())) {
            log.warn("Bank creation conflict: name '{}' already exists", dto.getName());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Institution name '" + dto.getName() + "' is already registered.");
        }

        if (participantBankRepository.existsByBic(dto.getBic())) {
            log.warn("Bank creation conflict: BIC '{}' already exists", dto.getBic());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BIC '" + dto.getBic() + "' is already registered.");
        }

        ParticipantBank bank = ParticipantBank.builder()
                .name(dto.getName().trim())
                .bic(dto.getBic().trim().toUpperCase())
                .shortName(dto.getShortName())
                .address(dto.getAddress())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .logoPath(dto.getLogoPath())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();
        bank.setCreatedBy(1L);

        ParticipantBank saved = participantBankRepository.save(bank);
        log.info("Successfully created participant bank '{}' with ID: {}", saved.getName(), saved.getId());
        return saved;
    }

    @Transactional
    public ParticipantBank updateBank(Long id, ParticipantBankDto dto) {
        log.info("Updating participant bank ID: {}", id);

        ParticipantBank existing = participantBankRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Participant bank not found with ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant bank with ID " + id + " not found");
                });

        if (dto.getName() != null && !dto.getName().isBlank()) {
            existing.setName(dto.getName().trim());
        }
        if (dto.getBic() != null && !dto.getBic().isBlank()) {
            existing.setBic(dto.getBic().trim().toUpperCase());
        }
        if (dto.getShortName() != null) {
            existing.setShortName(dto.getShortName().trim());
        }
        if (dto.getAddress() != null) {
            existing.setAddress(dto.getAddress().trim());
        }
        if (dto.getEmail() != null) {
            existing.setEmail(dto.getEmail().trim());
        }
        if (dto.getPhoneNumber() != null) {
            existing.setPhoneNumber(dto.getPhoneNumber().trim());
        }
        if (dto.getLogoPath() != null) {
            existing.setLogoPath(dto.getLogoPath().trim());
        }
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus().trim());
        }
        existing.setModifiedBy(1L);

        ParticipantBank updated = participantBankRepository.save(existing);
        log.info("Successfully updated participant bank '{}' (ID: {})", updated.getName(), updated.getId());
        return updated;
    }

    @Transactional(readOnly = true)
    public ParticipantBank getBankById(Long id) {
        log.info("Fetching participant bank by ID: {}", id);
        return participantBankRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Bank not found for ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant bank with ID " + id + " not found");
                });
    }

    @Transactional(readOnly = true)
    public ParticipantBank getBankByBic(String bic) {
        log.info("Fetching participant bank by BIC: {}", bic);
        return participantBankRepository.findByBic(bic.toUpperCase())
                .orElseThrow(() -> {
                    log.warn("Bank not found for BIC: {}", bic);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant bank with BIC " + bic + " not found");
                });
    }

    @Transactional(readOnly = true)
    public List<ParticipantBank> getAllBanks() {
        log.info("Fetching all participant banks");
        List<ParticipantBank> banks = participantBankRepository.findAll();
        log.info("Loaded {} participant banks", banks.size());
        return banks;
    }

    @Transactional(readOnly = true)
    public List<ParticipantBank> searchBanks(String query) {
        log.info("Searching participant banks with query: {}", query);
        List<ParticipantBank> results = participantBankRepository.searchByQuery(query != null ? query.trim() : "");
        log.info("Search returned {} results for query: '{}'", results.size(), query);
        return results;
    }

    @Transactional
    public void deleteBank(Long id) {
        log.info("Deleting participant bank with ID: {}", id);
        if (!participantBankRepository.existsById(id)) {
            log.warn("Cannot delete non-existent bank ID: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant bank with ID " + id + " not found");
        }
        participantBankRepository.deleteById(id);
        log.info("Successfully deleted participant bank ID: {}", id);
    }

    @Transactional
    public ParticipantBank updateBankLogo(Long id, org.springframework.web.multipart.MultipartFile logoFile) {
        log.info("Updating logo for participant bank ID: {}", id);
        ParticipantBank bank = getBankById(id);
        String logoPath = fileStorageUtil.saveLogoFile(bank.getBic(), logoFile);
        bank.setLogoPath(logoPath);
        bank.setModifiedBy(1L);
        ParticipantBank updated = participantBankRepository.save(bank);
        log.info("Logo updated successfully for bank '{}' (ID: {}): {}", updated.getName(), id, logoPath);
        return updated;
    }
}
