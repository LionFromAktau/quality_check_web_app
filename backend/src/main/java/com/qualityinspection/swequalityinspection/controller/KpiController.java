package com.qualityinspection.swequalityinspection.controller;

import com.qualityinspection.swequalityinspection.service.KpiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/kpi")
public class KpiController {

    @Autowired
    private KpiService kpiService;

    @GetMapping
    public Map<String, Long> getKpiStats() {
        return kpiService.getKpiStats();
    }
}
