// ===============================
// SERVICE BASE URLS
// ===============================
const DOCTOR_BASE = "http://localhost:8081";
const PATIENT_BASE = "http://localhost:8082";
const BOOKING_BASE = "http://localhost:8083";

const $ = (id) => document.getElementById(id);

function setOut(preEl, data) {
    preEl.textContent = typeof data === "string" ? data : JSON.stringify(data, null, 2);
}

async function readBody(res) {
    const ct = res.headers.get("content-type") || "";
    if (ct.includes("application/json")) return await res.json();
    return await res.text();
}

// ===============================
// Tabs
// ===============================
document.querySelectorAll(".tab").forEach(btn => {
    btn.addEventListener("click", () => {
        document.querySelectorAll(".tab").forEach(b => b.classList.remove("active"));
        document.querySelectorAll(".tabPanel").forEach(p => p.classList.remove("active"));
        btn.classList.add("active");
        $(btn.dataset.tab).classList.add("active");
    });
});

// ===============================
// Outputs
// ===============================
const doctorOut = $("doctorOut");
const scheduleOut = $("scheduleOut");
const patientOut = $("patientOut");
const bookingOut = $("bookingOut");
const bookingDetailsOut = $("bookingDetailsOut");

// ===============================
// State
// ===============================
let currentDoctor = null;   // holds loaded/created doctor object
let currentPatient = null;  // holds loaded/created patient object

// ===============================
// 1) DOCTOR: CREATE
// POST /api/v1/doctor/create
// ===============================
$("createDoctorBtn").addEventListener("click", async () => {
    setOut(doctorOut, "Creating doctor...");

    const payload = {
        name: $("docName").value.trim(),
        specialization: $("docSpec").value.trim(),
        qualification: $("docQual").value.trim(),
        contact: $("docContact").value.trim(),
        address: $("docAddress").value.trim()
    };

    if (!payload.name || !payload.specialization || !payload.qualification || !payload.contact || !payload.address) {
        setOut(doctorOut, "Fill all doctor fields first.");
        return;
    }

    try {
        const res = await fetch(`${DOCTOR_BASE}/api/v1/doctor/create`, {
            method: "POST",
            headers: {"Content-Type":"application/json"},
            body: JSON.stringify(payload)
        });

        const body = await readBody(res);
        if (!res.ok) {
            setOut(doctorOut, body);
            return;
        }

        currentDoctor = body;
        $("currentDoctorId").value = currentDoctor.id || "";
        setOut(doctorOut, currentDoctor);

    } catch (e) {
        setOut(doctorOut, {error:"REQUEST_FAILED", message: e.message});
    }
});

$("clearDoctorFormBtn").addEventListener("click", () => {
    ["docName","docSpec","docQual","docContact","docAddress","loadDoctorId","currentDoctorId"].forEach(id => $(id).value = "");
    currentDoctor = null;
    setOut(doctorOut, "Cleared.");
});

// ===============================
// 1) DOCTOR: LOAD BY ID
// GET /api/v1/doctor/getdoctorbyid?doctorId=1
// ===============================
$("loadDoctorBtn").addEventListener("click", async () => {
    const doctorId = Number($("loadDoctorId").value);
    if (!doctorId) { setOut(doctorOut, "Enter doctorId first."); return; }

    setOut(doctorOut, "Loading doctor...");
    try {
        const res = await fetch(`${DOCTOR_BASE}/api/v1/doctor/getdoctorbyid?doctorId=${encodeURIComponent(doctorId)}`);
        const body = await readBody(res);

        if (!res.ok) { setOut(doctorOut, body); return; }

        currentDoctor = body;
        $("currentDoctorId").value = currentDoctor.id || "";
        setOut(doctorOut, currentDoctor);

    } catch (e) {
        setOut(doctorOut, {error:"REQUEST_FAILED", message: e.message});
    }
});

// ===============================
// 2) SCHEDULE: helper (use current doctor id)
// ===============================
$("useCurrentDoctorForScheduleBtn").addEventListener("click", () => {
    if (!currentDoctor?.id) { setOut(scheduleOut, "No current doctor. Create/load doctor first."); return; }
    $("scheduleDoctorId").value = currentDoctor.id;
    setOut(scheduleOut, `Using current doctorId=${currentDoctor.id}`);
});

// ===============================
// 2) ADD SCHEDULE
// POST /api/v1/doctor/{doctorId}/schedule
// Body: { "date":"YYYY-MM-DD", "times":["10:00","10:30"] }
// ===============================
$("addScheduleBtn").addEventListener("click", async () => {
    const doctorId = Number($("scheduleDoctorId").value);
    const date = $("scheduleDate").value; // yyyy-mm-dd
    const timesRaw = $("scheduleTimes").value;

    if (!doctorId || !date || !timesRaw.trim()) {
        setOut(scheduleOut, "Fill doctorId + date + times first.");
        return;
    }

    const times = timesRaw.split(",").map(t => t.trim()).filter(Boolean);

    // Quick validation
    const bad = times.find(t => !/^\d{2}:\d{2}$/.test(t));
    if (bad) {
        setOut(scheduleOut, `Invalid time format: "${bad}". Use HH:MM like 10:30`);
        return;
    }

    const payload = { date, times };

    setOut(scheduleOut, "Adding schedule...");
    try {
        const res = await fetch(`${DOCTOR_BASE}/api/v1/doctor/${encodeURIComponent(doctorId)}/schedule`, {
            method: "POST",
            headers: {"Content-Type":"application/json"},
            body: JSON.stringify(payload)
        });

        const body = await readBody(res);
        if (!res.ok) { setOut(scheduleOut, body); return; }

        setOut(scheduleOut, {message:"Schedule added", schedule: body});

    } catch (e) {
        setOut(scheduleOut, {error:"REQUEST_FAILED", message: e.message});
    }
});

// ===============================
// 2) LOAD DOCTOR SCHEDULES (for checking)
// GET /api/v1/doctor/getdoctorbyid?doctorId=
// ===============================
$("loadDoctorSchedulesBtn").addEventListener("click", async () => {
    const doctorId = Number($("scheduleDoctorId").value || $("currentDoctorId").value);
    if (!doctorId) { setOut(scheduleOut, "Enter doctorId first."); return; }

    setOut(scheduleOut, "Loading doctor schedules...");
    try {
        const res = await fetch(`${DOCTOR_BASE}/api/v1/doctor/getdoctorbyid?doctorId=${encodeURIComponent(doctorId)}`);
        const body = await readBody(res);
        if (!res.ok) { setOut(scheduleOut, body); return; }

        currentDoctor = body;
        $("currentDoctorId").value = currentDoctor.id || "";
        setOut(scheduleOut, currentDoctor?.schedules ? currentDoctor.schedules : body);

    } catch (e) {
        setOut(scheduleOut, {error:"REQUEST_FAILED", message: e.message});
    }
});

// ===============================
// 3) PATIENT: CREATE
// POST /api/v1/patient/create
// ===============================
$("createPatientBtn").addEventListener("click", async () => {
    setOut(patientOut, "Creating patient...");

    const payload = {
        name: $("patName").value.trim(),
        email: $("patEmail").value.trim(),
        contact: $("patContact").value.trim()
    };

    if (!payload.name || !payload.email || !payload.contact) {
        setOut(patientOut, "Fill all patient fields first.");
        return;
    }

    try {
        const res = await fetch(`${PATIENT_BASE}/api/v1/patient/create`, {
            method: "POST",
            headers: {"Content-Type":"application/json"},
            body: JSON.stringify(payload)
        });

        const body = await readBody(res);
        if (!res.ok) { setOut(patientOut, body); return; }

        currentPatient = body;
        $("currentPatientId").value = currentPatient.id || "";
        setOut(patientOut, currentPatient);

    } catch (e) {
        setOut(patientOut, {error:"REQUEST_FAILED", message: e.message});
    }
});

$("clearPatientFormBtn").addEventListener("click", () => {
    ["patName","patEmail","patContact","loadPatientId","currentPatientId"].forEach(id => $(id).value = "");
    currentPatient = null;
    setOut(patientOut, "Cleared.");
});

// ===============================
// 3) PATIENT: LOAD BY ID
// GET /api/v1/patient/getpatientbyid?id=
// ===============================
$("loadPatientBtn").addEventListener("click", async () => {
    const patientId = Number($("loadPatientId").value);
    if (!patientId) { setOut(patientOut, "Enter patientId first."); return; }

    setOut(patientOut, "Loading patient...");
    try {
        const res = await fetch(`${PATIENT_BASE}/api/v1/patient/getpatientbyid?id=${encodeURIComponent(patientId)}`);
        const body = await readBody(res);
        if (!res.ok) { setOut(patientOut, body); return; }

        currentPatient = body;
        $("currentPatientId").value = currentPatient.id || "";
        setOut(patientOut, currentPatient);

    } catch (e) {
        setOut(patientOut, {error:"REQUEST_FAILED", message: e.message});
    }
});

// ===============================
// 4) BOOKING: helper buttons
// ===============================
$("useCurrentDoctorForBookingBtn").addEventListener("click", () => {
    if (!currentDoctor?.id) { setOut(bookingOut, "No current doctor. Create/load doctor first."); return; }
    $("bookDoctorId").value = currentDoctor.id;
    setOut(bookingOut, `Using current doctorId=${currentDoctor.id}`);
});

$("useCurrentPatientForBookingBtn").addEventListener("click", () => {
    if (!currentPatient?.id) { setOut(bookingOut, "No current patient. Create/load patient first."); return; }
    $("bookPatientId").value = currentPatient.id;
    setOut(bookingOut, `Using current patientId=${currentPatient.id}`);
});

// ===============================
// 4) BOOKING: Load doctor & populate dates dropdown
// GET doctor by id → schedules → date list
// ===============================
$("loadBookingDoctorBtn").addEventListener("click", async () => {
    const doctorId = Number($("bookDoctorId").value || $("currentDoctorId").value);
    if (!doctorId) { setOut(bookingOut, "Enter doctorId first."); return; }

    setOut(bookingOut, "Loading doctor schedules for booking...");
    try {
        const res = await fetch(`${DOCTOR_BASE}/api/v1/doctor/getdoctorbyid?doctorId=${encodeURIComponent(doctorId)}`);
        const body = await readBody(res);
        if (!res.ok) { setOut(bookingOut, body); return; }

        currentDoctor = body;

        // Fill date dropdown from schedules
        const dateSelect = $("bookDateSelect");
        dateSelect.innerHTML = `<option value="">-- Select Date --</option>`;

        const schedules = currentDoctor.schedules || [];
        schedules.forEach(s => {
            const opt = document.createElement("option");
            opt.value = s.date;            // "YYYY-MM-DD"
            opt.textContent = s.date;
            opt.dataset.scheduleId = s.id; // schedule id
            dateSelect.appendChild(opt);
        });

        $("bookTimeSelect").innerHTML = `<option value="">-- Choose date first --</option>`;
        setOut(bookingOut, {message:"Dates loaded", doctorId, totalSchedules: schedules.length});

    } catch (e) {
        setOut(bookingOut, {error:"REQUEST_FAILED", message: e.message});
    }
});

// When date changes → load slots by scheduleId
$("bookDateSelect").addEventListener("change", async () => {
    const dateSelect = $("bookDateSelect");
    const selectedOpt = dateSelect.options[dateSelect.selectedIndex];
    const scheduleId = selectedOpt?.dataset?.scheduleId;

    const timeSelect = $("bookTimeSelect");
    timeSelect.innerHTML = `<option value="">Loading slots...</option>`;

    if (!scheduleId) {
        timeSelect.innerHTML = `<option value="">-- Choose date first --</option>`;
        return;
    }

    try {
        const res = await fetch(`${DOCTOR_BASE}/api/v1/doctor/${encodeURIComponent(scheduleId)}/slots`);
        const body = await readBody(res);
        if (!res.ok) {
            timeSelect.innerHTML = `<option value="">Failed to load slots</option>`;
            setOut(bookingOut, body);
            return;
        }

        // body is list of TimeSlots {id,time}
        timeSelect.innerHTML = `<option value="">-- Select Time --</option>`;
        body.forEach(slot => {
            const opt = document.createElement("option");
            opt.value = slot.time;       // "HH:MM:SS" or "HH:MM"
            opt.textContent = slot.time;
            timeSelect.appendChild(opt);
        });

        setOut(bookingOut, {message:"Slots loaded", scheduleId, slots: body});

    } catch (e) {
        timeSelect.innerHTML = `<option value="">Failed to load slots</option>`;
        setOut(bookingOut, {error:"REQUEST_FAILED", message: e.message});
    }
});

// ===============================
// 4) CREATE BOOKING & PAY
// POST /api/v1/booking/create?doctorId=&patientId=&date=&time=
// returns StripeResponse with sessionUrl → redirect
// ===============================
$("createBookingBtn").addEventListener("click", async () => {
    const doctorId = Number($("bookDoctorId").value);
    const patientId = Number($("bookPatientId").value);
    const date = $("bookDateSelect").value;
    let time = $("bookTimeSelect").value;

    if (!doctorId || !patientId || !date || !time) {
        setOut(bookingOut, "Fill doctorId + patientId + date + time first (use Load Doctor & Dates).");
        return;
    }

    // Your backend expects LocalTime like "10:30" or "10:30:00"
    // If it returns "10:30:00", keep it. If it returns "10:30", also fine.
    setOut(bookingOut, "Creating booking...");

    try {
        const url =
            `${BOOKING_BASE}/api/v1/booking/create` +
            `?doctorId=${encodeURIComponent(doctorId)}` +
            `&patientId=${encodeURIComponent(patientId)}` +
            `&date=${encodeURIComponent(date)}` +
            `&time=${encodeURIComponent(time)}`;

        const res = await fetch(url, { method: "POST" });
        const body = await readBody(res);

        if (!res.ok) { setOut(bookingOut, body); return; }

        setOut(bookingOut, body);

        if (body?.sessionUrl) {
            // redirect to Stripe checkout
            window.location.href = body.sessionUrl;
        } else {
            setOut(bookingOut, {warning:"No sessionUrl returned", response: body});
        }

    } catch (e) {
        setOut(bookingOut, {error:"REQUEST_FAILED", message: e.message});
    }
});

$("resetBookingBtn").addEventListener("click", () => {
    ["bookDoctorId","bookPatientId","checkBookingId"].forEach(id => $(id).value = "");
    $("bookDateSelect").innerHTML = `<option value="">-- Load doctor schedules first --</option>`;
    $("bookTimeSelect").innerHTML = `<option value="">-- Choose date first --</option>`;
    setOut(bookingOut, "Reset done.");
});

// ===============================
// OPTIONAL: GET BOOKING BY ID
// GET /api/v1/booking/by-id?bookingId=
// ===============================
$("checkBookingBtn").addEventListener("click", async () => {
    const bookingId = Number($("checkBookingId").value);
    if (!bookingId) { setOut(bookingDetailsOut, "Enter bookingId first."); return; }

    setOut(bookingDetailsOut, "Loading booking...");
    try {
        const res = await fetch(`${BOOKING_BASE}/api/v1/booking/by-id?bookingId=${encodeURIComponent(bookingId)}`);
        const body = await readBody(res);
        if (!res.ok) { setOut(bookingDetailsOut, body); return; }

        setOut(bookingDetailsOut, body);
    } catch (e) {
        setOut(bookingDetailsOut, {error:"REQUEST_FAILED", message: e.message});
    }
});