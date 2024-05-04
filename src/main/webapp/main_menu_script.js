// Deontae Cocroft | 2024 May 4th
document.addEventListener('DOMContentLoaded', function() {

    // Menu options
    document.getElementById('patientsButton').addEventListener('click', patients);
    document.getElementById('billingButton').addEventListener('click', billing);
    document.getElementById('prescriptionButton').addEventListener('click', prescription);
    document.getElementById('appointmentButton').addEventListener('click', appointments);
    document.getElementById('logoutButton').addEventListener('click', logout);

    function patients() {
        window.location.href = 'patients.html';
    };

   function billing() {
        window.location.href = 'billing.html';
    };

    function prescription() {
        window.location.href = 'prescription.html';
    };

    function appointments(){
        window.location.href = 'appointment.html';
    };

    function logout(){
        window.location.href = 'login.html';
    };
});
