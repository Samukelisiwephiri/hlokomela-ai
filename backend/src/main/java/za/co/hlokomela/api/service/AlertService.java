package za.co.hlokomela.api.service;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.hlokomela.api.domain.Alert;
import za.co.hlokomela.api.domain.UserAccount;
import za.co.hlokomela.api.exception.ResourceNotFoundException;
import za.co.hlokomela.api.repository.AlertRepository;

@Service
public class AlertService {
    private final AlertRepository alerts;

    public AlertService(AlertRepository alerts) { this.alerts = alerts; }

    @Transactional(readOnly = true)
    public List<Alert> list(UserAccount account, Pageable pageable) {
        return alerts.findByMunicipalityIdOrderByCreatedAtDesc(account.getMunicipality().getId(), pageable);
    }

    @Transactional
    public Alert markRead(UserAccount account, UUID id) {
        Alert alert = alerts.findById(id).orElseThrow(() -> new ResourceNotFoundException("Alert was not found"));
        if (!alert.getMunicipality().getId().equals(account.getMunicipality().getId())) {
            throw new ResourceNotFoundException("Alert was not found");
        }
        alert.markRead();
        return alerts.save(alert);
    }
}
