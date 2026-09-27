# Mana Community Safety Network Microservice

Next-Generation Gated Community Safety, Visitor, Vehicle, Gate & Hardware Integration Network.

## 🚀 Capabilities

- 📊 **Security Command Dashboard**: Real-time gate stream, active visitors, deliveries, duty guards, vehicle entries/exits, parking violations.
- 👥 **Visitor Management & Passes**: Pre-approved 6-digit / QR passes, walk-in guard approval requests with resident approval prompts, overstay tracking.
- 🚗 **Vehicle Management & Parking Enforcement**: Resident vehicle registry, ANPR & RFID access logging, parking violation ticketing with photo evidence & penalty calculation.
- 📦 **Deliveries & Parcel Management**: Doorstep vs Leave-at-Gate locker drop-off, OTP collection verification.
- 🧑‍🔧 **Domestic Staff & Guard Roster**: Daily help registry, attendance clock-in/out (RFID/QR/Biometric), guard patrol route checkpoint scanning.
- 🚨 **Safety Incidents & Gate SOS**: Incident logging, investigation updates, rapid gate emergency override.
- 🔌 **Hardware & AI Integration Ready**:
  - **ANPR**: License plate recognition camera webhook & automatic boom barrier actuation.
  - **RFID**: Fast vehicle and staff RFID card reader ingestion.
  - **CCTV & AI Analytics**: Perimeter breach, loitering, and speeding alert webhook.
  - **Access Control & Turnstiles**: Unified interface for door controllers & barriers.

## 🛠️ Tech Stack
- Java 17
- Spring Boot 3.4.1
- Spring Data JPA (PostgreSQL)
- Spring Security (JJWT Stateless Auth)
- Spring WebSocket / STOMP Realtime Gateway
- Maven

## 🔌 API Endpoints Base
- `/api/v1/safety/dashboard`
- `/api/v1/safety/visitors`
- `/api/v1/safety/vehicles`
- `/api/v1/safety/parking`
- `/api/v1/safety/deliveries`
- `/api/v1/safety/staff`
- `/api/v1/safety/patrols`
- `/api/v1/safety/incidents`
- `/api/v1/safety/sos`
- `/api/v1/safety/integrations`
- `/ws-safety` (WebSocket STOMP Broker)
