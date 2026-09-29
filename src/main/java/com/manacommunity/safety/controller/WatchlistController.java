package com.manacommunity.safety.controller;

import com.manacommunity.safety.domain.entities.SecurityWatchlist;
import com.manacommunity.safety.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/security/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @GetMapping
    public ResponseEntity<List<SecurityWatchlist>> getWatchlist(
            @RequestHeader(value = "X-Community-Id", defaultValue = "1") Long communityId,
            @RequestParam(value = "activeOnly", defaultValue = "true") boolean activeOnly) {
        if (activeOnly) {
            return ResponseEntity.ok(watchlistService.getActiveWatchlist(communityId));
        }
        return ResponseEntity.ok(watchlistService.getAllEntries(communityId));
    }

    @PostMapping("/request")
    public ResponseEntity<SecurityWatchlist> requestEntry(
            @RequestHeader(value = "X-Community-Id", defaultValue = "1") Long communityId,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @RequestHeader(value = "X-User-Name", defaultValue = "Security Officer") String userName,
            @RequestBody Map<String, String> body) {
        SecurityWatchlist entry = watchlistService.requestWatchlistEntry(
                communityId,
                userId,
                userName,
                body.get("subjectName"),
                body.get("phone"),
                body.get("licensePlate"),
                body.get("idProofNumber"),
                body.get("reason"),
                body.get("auditNotes")
        );
        return ResponseEntity.ok(entry);
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<SecurityWatchlist> reviewEntry(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long adminId,
            @RequestHeader(value = "X-User-Name", defaultValue = "Admin") String adminName,
            @RequestBody Map<String, Object> body) {
        boolean approve = Boolean.parseBoolean(String.valueOf(body.get("approve")));
        String notes = (String) body.get("notes");
        return ResponseEntity.ok(watchlistService.reviewWatchlistEntry(id, adminId, adminName, approve, notes));
    }
}
