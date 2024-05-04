// Deontae Cocroft | 2024 May 4th
window.onload = function() {

    // Fetches table information upon window opening
    fetchPatients();
    // Add patient
    document.getElementById('addPatientForm').addEventListener('submit', addPatient);

    // Delete
    document.getElementById('deletePatientButton').addEventListener('click', showDeletePrompt);
    document.getElementById('deletePatientForm').addEventListener('submit', deletePatient);
    document.getElementById('cancelDeleteButton').addEventListener('click', cancelDeletePrompt);
    document.getElementById('clearDeleteButton').addEventListener('click', clearDeleteField);

    // Update
    document.getElementById('updatePatientForm').addEventListener('submit', updatePatient);
    document.getElementById('updatePatientButton').addEventListener('click', showUpdatePrompt);
    document.getElementById('cancelUpdateButton').addEventListener('click', cancelUpdatePrompt);
    document.getElementById('clearUpdateButton').addEventListener('click', clearUpdateField);

    // Search
    document.getElementById('searchPatientButton').addEventListener('click', searchPatients);

    // Clear Fields
    document.getElementById('clearFieldsButton').addEventListener('click', clearFields);

    // Main Menu
    document.getElementById('mainMenuButton').addEventListener('click',mainMenu);

    // Help
    document.getElementById('helpButton').addEventListener('click', helpPrompt);
};

// Functions for clearing forms, showing prompts, or hiding prompts

// Clear
function clearFields() {
    document.getElementById('addPatientForm').reset();
}

// Update
function clearUpdateField(){
    document.getElementById('updatePatientForm').reset();
}

function showUpdatePrompt() {
    document.getElementById('updatePrompt').style.display = 'block';
}

function cancelUpdatePrompt() {
    document.getElementById('updatePrompt').style.display = 'none';
}

// Delete
function clearDeleteField(){
    document.getElementById('deletePatientForm').reset();
}

function showDeletePrompt() {
    document.getElementById('deletePrompt').style.display = 'block';
}

function cancelDeletePrompt() {
    document.getElementById('deletePrompt').style.display = 'none';
}

// Main Menu
function mainMenu(){
    window.location.href = 'main_menu.html';
}

// Help
function helpPrompt(){
    alert('To search Patients only use First Name, Last Name, or Phone Number Fields and click search.' + '\n\nTo delete Patients only use Patient ID field is popup after clicking delete button.' + '\n\nClick Xray Image link next to patient to view xray images'
            +'\n\nTo update patient information type Patient ID after popup and put data into fields that need to be changed.' + 
            '\n\nEnsure all fields marked with * are filled out. Ensure correct numerical value for Zip Code, Insurance Number, Phone Number, and Patient ID.' +
            '\n\nEnsure state field is an abbreviation for the state. EX: Illinois = IL.' )

}

// Functions responsible for validation
function validateZipCode(zipCode) {
    return /^\d{5}$/.test(zipCode);
}

function validateState(state) {
    return /^[A-Za-z]{2}$/.test(state);
}

function validateGender(gender) {
    return /^[mMfF]{1}$/.test(gender);
}

// Function that takes input and communicated with java to update patient information
function updatePatient(event) {
    event.preventDefault();
    // Get the patientId for patient that will be updated.
    const patientId = document.getElementById('updatePatientId').value;

    // Turn the add patient for into data for an update
    const form = document.getElementById('addPatientForm');
    const formData = new FormData(form);

    // Trim values and delete them if there is no entry
    const data = {};
    for (const [key, value] of formData.entries()) {
        if (!value.trim()) {
            formData.delete(key);
        }
    }

    // Validation for other fields in addPatientForm
    if (data.zipCode && !validateZipCode(data.zipCode)) {
        alert('Please enter a valid 5-digit zip code.');
        return;
    }

    if (data.state && !validateState(data.state)) {
        alert('Please enter a valid state abbreviation.');
        return;
    }

    if (data.gender && !validateGender(data.gender)) {
        alert('Please enter a valid gender (M/F).');
        return;
    }

    
    // Fetches the POST method to update patient by patientId
    fetch(`updatePatient?patientId=${patientId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams(formData).toString()
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.statusText);
        }
        // Refreshes the table, sends message, reset forms, and hide update popup
        fetchPatients(); 
        alert('Patient updated successfully.');
        document.getElementById('updatePatientForm').reset();
        document.getElementById('updatePrompt').style.display = 'none';
        document.getElementById('addPatientForm').reset();
    })
    .catch(error => {
        console.error('Error updating patient:', error);
        alert('Error updating patient. Please try again.');
    });
}

// Function that fetches patient information for html table
function fetchPatients() {
    // Fetch patients
    fetch('patients') 
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            if (response.status === 204) {
                return [];
            }
            return response.json();
        })
        .then(data => {
            const tbody = document.querySelector('#patientsTable tbody');
            tbody.innerHTML = '';
            
            // Layout of table for receive information from java and send it back to html table
            data.forEach(patient => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${patient.patientId}</td>
                    <td>${patient.firstName}</td>
                    <td>${patient.lastName}</td>
                    <td>${patient.dateOfBirth}</td>
                    <td>${patient.gender}</td>
                    <td>${patient.address}</td>
                    <td>${patient.city}</td>
                    <td>${patient.state}</td>
                    <td>${patient.zipCode}</td>
                    <td>${patient.insuranceCompany}</td>
                    <td>${patient.insuranceNumber}</td>
                    <td>${patient.phoneNumber}</td>
                    <td>${patient.notes}</td>
                    <td>${patient.xrayImages ? `<a href="${patient.xrayImages}" target="_blank">${patient.xrayImages}</a>` : ''}</td>
                `;
                tbody.appendChild(row);
            });
        })
        .catch(error => {
            console.error('Error fetching patients:', error);
        });
}

// Function that allows users to add new patients
function addPatient(event) {
    event.preventDefault();

    // Get data from the addPatientForm
    const form = new FormData(document.getElementById('addPatientForm'));

    // Validate some fields in addPatientForm
    if (!validateZipCode(form.get('zipCode'))) {
        alert('Please enter a valid 5 digit zip code.');
        return;
    }

    if (!validateState(form.get('state'))) {
        alert('Please enter a valid state abbreviation.');
        return;
    }

    if (!validateGender(form.get('gender'))) {
        alert('Please enter a valid gender (M/F).');
        return;
    }

    // Fetch POST java method to take information entered in HTML and add patient to the database
    fetch('addPatient', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams(form).toString()
    })

    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.statusText);
        }
        // Updates table, sends message, and resets the addPatientForm after patient is added.
        fetchPatients();
        alert('Patient added successfully.');
        document.getElementById('addPatientForm').reset();
    })
    .catch(error => {
        console.error('Error adding patient:', error);
        alert('Error adding patient. Please try again.');
    });
}

// Function that searches patient in the database
function searchPatients() {
    // Getting the values from the fields that will be used to search patients
    const firstName = document.getElementById('firstName').value;
    const lastName = document.getElementById('lastName').value;
    const phoneNumber = document.getElementById('phoneNumber').value;

    // Fetches method to search patients by firstName, lastName, and phoneNumber
    fetch(`searchPatients?firstName=${firstName}&lastName=${lastName}&phoneNumber=${phoneNumber}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            const tbody = document.querySelector('#patientsTable tbody');
            tbody.innerHTML = '';
            
            // After values above are submitted to java the table is updated to show patient based on search.
            data.forEach(patient => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${patient.patientId}</td>
                    <td>${patient.firstName}</td>
                    <td>${patient.lastName}</td>
                    <td>${patient.dateOfBirth}</td>
                    <td>${patient.gender}</td>
                    <td>${patient.address}</td>
                    <td>${patient.city}</td>
                    <td>${patient.state}</td>
                    <td>${patient.zipCode}</td>
                    <td>${patient.insuranceCompany}</td>
                    <td>${patient.insuranceNumber}</td>
                    <td>${patient.phoneNumber}</td>
                    <td>${patient.notes}</td>
                    <td>${patient.xrayImages ? `<a href="${patient.xrayImages}" target="_blank">${patient.xrayImages}</a>` : ''}</td>
                `;
                tbody.appendChild(row);
            });
        })
        .catch(error => {
            console.error('Error searching patients:', error);
            alert('Error searching patients. Please try again.');
        });
}

// Code that deletes patients from database.
function deletePatient(event) {
    event.preventDefault();

    // Get the value of patientId to delete
    const patientId = document.getElementById('deletePatientId').value;

    // Calls java DELETE method and sends patientID value so java can remove patient.
    fetch(`deletePatient?patientId=${patientId}`, {
        method: 'DELETE',
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.statusText);
        }
        // Provides message, updates table, resets form, and closes delete prompt after patient is deleted successfully.
        alert('Patient: ' + patientId + ' deleted successfully or did not exist.');
        fetchPatients();
        document.getElementById('deletePrompt').style.display = 'none';
        document.getElementById('deletePatientForm').reset();
    })
    .catch(error => {
        console.error('Error deleting patient:', error);
        alert('Error deleting patient. Patient may have appointment or bills. Please try again.');
    });
}


