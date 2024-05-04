// Deontae Cocroft | 2024 May 4th
window.onload = function(){
    
// Fetches table information upon window opening.
fetchAppointments();
fetchProcedures();
fetchDentalHygienist();
fetchDentalAssistant();
fetchDentist();
fetchDentalSurgeon();

// Add bill
document.getElementById('addAppointmentForm').addEventListener('submit' , addAppointment);

// Delete
document.getElementById('deleteAppointmentForm').addEventListener('submit', deleteAppointment);
document.getElementById('clearDeleteButton').addEventListener('click', clearDeleteField);
document.getElementById('deleteAppointmentButton').addEventListener('click', showDeletePrompt);
document.getElementById('cancelDeleteButton').addEventListener('click', cancelDeletePrompt);

// Search
document.getElementById('searchAppointmentPrompt').addEventListener('submit', searchAppointment);
document.getElementById('clearSearchButton').addEventListener('click', clearSearchField);
document.getElementById('searchAppointmentButton').addEventListener('click', showSearchPrompt);
document.getElementById('cancelSearchButton').addEventListener('click', cancelSearchPrompt);

// Cancel
document.getElementById('cancelAppointmentForm').addEventListener('submit', cancelAppointment);
document.getElementById('cancelButton').addEventListener('click', showCancelPrompt);
document.getElementById('clearCancelAppointmentButton').addEventListener('click', clearCancelField)
document.getElementById('cancelCancelAppointmentButton').addEventListener('click', cancelCancelPrompt);

//Patients
document.getElementById('patientButton').addEventListener('click', patients);
// Clear
document.getElementById('clearFieldsButton').addEventListener('click', clearFields);

// Main Menu
document.getElementById('mainMenuButton').addEventListener('click',mainMenu);

// Help
document.getElementById('helpButton').addEventListener('click', helpPrompt);
};


// Delete
function clearDeleteField() {
    document.getElementById('deleteAppointmentForm').reset();
}

function showDeletePrompt(){
    document.getElementById('deleteAppointmentPrompt').style.display = 'block';
}

function cancelDeletePrompt(){
    document.getElementById('deleteAppointmentPrompt').style.display = 'none';
}

// Search
function clearSearchField() {
    document.getElementById('searchAppointmentForm').reset();
}

function showSearchPrompt() {
    document.getElementById('searchAppointmentPrompt').style.display = 'block';
}

function cancelSearchPrompt() {
    document.getElementById('searchAppointmentPrompt').style.display = 'none';
}

// Cancel Appointment
function clearCancelField() {
    document.getElementById('cancelAppointmentForm').reset();
}

function showCancelPrompt() {
    document.getElementById('cancelAppointmentPrompt').style.display = 'block';
}

function cancelCancelPrompt() {
    document.getElementById('cancelAppointmentPrompt').style.display = 'none';
}

// Clear
function clearFields () {
    document.getElementById('addAppointmentForm').reset();
}

// Main Menu
function mainMenu(){
    window.location.href = 'main_menu.html';
}

//Patients
function patients(){
    window.location.href = 'patients.html';
}
// Help
function helpPrompt() {
    alert("To search Appointments only use Patient ID or Appointment ID Fields."+ 
    "\n\nTo delete Appointment click button and use the pop up Appointment ID field."+ 
    "\n\nEnsure dental specialists have a 30 min gap between appointments or appointment cannot be created."+
    "\n\nEnsure all fields marked with * are filled out."+ 
    "\n\nEnsure numerical value for all ID fields."+ 
    "\n\nTo mark appointment as canceled click cancel appointment button and use canceled field and Appointment ID field in pop up. Type 'true' in Canceled field to cancel appointment."+ 
    " Type 'false' to uncancel.")
}

// Validation
function validateCanceled(value) {
    const lowerCaseValue = value.toLowerCase();
    return lowerCaseValue === "true" || lowerCaseValue === "false";
}

// Function to display appointment information.
function fetchAppointments(){

    // Fetch appointment information.
    fetch('appointment')
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
        const tbody = document.querySelector('#appointmentTable tbody');
        tbody.innerHTML = '';
        
        // Layout of table for receive information from java and send it back to html table.
        data.forEach(appointment => {
            const row = document.createElement('tr');
            row.innerHTML = `
                    <td>${appointment.appointmentId}</td>
                    <td>${appointment.patientId}</td>
                    <td>${appointment.procedureId}</td>
                    <td>${appointment.procedureOccurrences}</td>
                    <td>${appointment.dentistId}</td>
                    <td>${appointment.dentalHygienistId}</td>
                    <td>${appointment.dentalAssistantId}</td>
                    <td>${appointment.dentalSurgeonId}</td>
                    <td>${appointment.appointmentDate}</td>
                    <td>${appointment.appointmentTime}</td>
                    <td>${appointment.notes}</td>
                    <td>${appointment.canceled}</td>
             
            `;
            tbody.appendChild(row);
        });
    })
    .catch(error => {
        console.error('Error fetching appointments:', error);
    });
}

// Function to display procedure information.
function fetchProcedures(){

    // Fetch procedure information.
    fetch('procedure')
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
        const tbody = document.querySelector('#procedureTable tbody');
        tbody.innerHTML = '';
        
        // Layout of table for receive information from java and send it back to html table.
        data.forEach(procedure => {
            const row = document.createElement('tr');
            row.innerHTML = `
                    <td>${procedure.procedureId}</td>
                    <td>${procedure.name}</td>
                    <td>${procedure.notes}</td>
                    <td>${procedure.price}</td>
             
            `;
            tbody.appendChild(row);
        });
    })
    .catch(error => {
        console.error('Error fetching procedures:', error);
    });
}

// Function to display dental hygienist information.
function fetchDentalHygienist(){

    // Fetch dental hygienist information.
    fetch('dentalHygienist')
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
        const tbody = document.querySelector('#dentalHygienistTable tbody');
        tbody.innerHTML = '';
        
        // Layout of table for receive information from java and send it back to html table.
        data.forEach(dentalHygienist => {
            const row = document.createElement('tr');
            row.innerHTML = `
                    <td>${dentalHygienist.dentalHygienistId}</td>
                    <td>${dentalHygienist.firstName}</td>
                    <td>${dentalHygienist.lastName}</td>
                    <td>${dentalHygienist.phoneNumber}</td>
             
            `;
            tbody.appendChild(row);
        });
    })
    .catch(error => {
        console.error('Error fetching dental hygienists:', error);
    });
}

// Function to display dental assistant information.
function fetchDentalAssistant(){

    // Fetch dental assistant information.
    fetch('dentalAssistant')
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
        const tbody = document.querySelector('#dentalAssistantTable tbody');
        tbody.innerHTML = '';
        
        // Layout of table for receive information from java and send it back to html table.
        data.forEach(dentalAssistant => {
            const row = document.createElement('tr');
            row.innerHTML = `
                    <td>${dentalAssistant.dentalAssistantId}</td>
                    <td>${dentalAssistant.firstName}</td>
                    <td>${dentalAssistant.lastName}</td>
                    <td>${dentalAssistant.phoneNumber}</td>
             
            `;
            tbody.appendChild(row);
        });
    })
    .catch(error => {
        console.error('Error fetching dental assistants:', error);
    });
}

// Function to display dentist information.
function fetchDentist(){

    // Fetch dentist information.
    fetch('dentist')
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
        const tbody = document.querySelector('#dentistTable tbody');
        tbody.innerHTML = '';
        
        // Layout of table for receive information from java and send it back to html table.
        data.forEach(dentist => {
            const row = document.createElement('tr');
            row.innerHTML = `
                    <td>${dentist.dentistId}</td>
                    <td>${dentist.firstName}</td>
                    <td>${dentist.lastName}</td>
                    <td>${dentist.phoneNumber}</td>
             
            `;
            tbody.appendChild(row);
        });
    })
    .catch(error => {
        console.error('Error fetching dentists:', error);
    });
}

// Function to display dental surgeon information.
function fetchDentalSurgeon(){

    // Fetch dental surgeon information.
    fetch('dentalSurgeon')
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
        const tbody = document.querySelector('#dentalSurgeonTable tbody');
        tbody.innerHTML = '';
        
        // Layout of table for receive information from java and send it back to html table.
        data.forEach(dentalSurgeon => {
            const row = document.createElement('tr');
            row.innerHTML = `
                    <td>${dentalSurgeon.dentalSurgeonId}</td>
                    <td>${dentalSurgeon.firstName}</td>
                    <td>${dentalSurgeon.lastName}</td>
                    <td>${dentalSurgeon.phoneNumber}</td>
             
            `;
            tbody.appendChild(row);
        });
    })
    .catch(error => {
        console.error('Error fetching dental surgeons:', error);
    });
}

// Function to add a new appointment
function addAppointment(event) {
    event.preventDefault();

    // Get form data
    const form = new FormData(document.getElementById('addAppointmentForm'));

    // Fetch POST request to add appointment
    fetch('addAppointment', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams(form).toString()
    })
    .then(response => {
        if (!response.ok) {
            alert('Error adding appointment. Please check the entered ID fields and ensure dental specialists have a 30 min gap between appointments. \n Ensure id fields exist.');
            throw new Error('Network response was not ok: ' + response.statusText);
        }
        // Update table, send message, reset form
        fetchAppointments();
        alert('Appointment added successfully.');
        document.getElementById('addAppointmentForm').reset();
    })
    .catch(error => {
        console.error('Error adding appointment:', error);
        alert('Error adding appointment. Please try again.');
    });
}

// Function that cancels appointments from the database.
function deleteAppointment(event) {
    event.preventDefault();

    // Get the value of the appointment ID to cancel.
    const appointmentId = document.getElementById('deleteAppointmentId').value;

    // Call Java DELETE method and send appointment ID value so Java can cancel the appointment.
    fetch(`deleteAppointment?appointmentId=${appointmentId}`, {
        method: 'DELETE',
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.statusText);
        }
        // Provide message, update table, reset form, and close delete prompt after the appointment is canceled successfully.
        alert('Appointment ID: ' + appointmentId + ' deleted successfully or did not exist.');
        fetchAppointments();
        document.getElementById('deleteAppointmentPrompt').style.display = 'none';
        document.getElementById('deleteAppointmentForm').reset();
    })
    .catch(error => {
        console.error('Error deleting appointment:', error);
        alert('Error deleting appointment. Please try again.');
    });
}

// Function that searches appointments in database.
function searchAppointment(event) {
    event.preventDefault();

    // Getting the values from the fields that will be used to search appointments.
    const patientId = document.getElementById('patientId').value;
    const appointmentId = document.getElementById('searchAppointmentId').value;

    // Fetch method to search appointments by patientId and appointmentId
    fetch(`searchAppointment?patientId=${patientId}&appointmentId=${appointmentId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            const tbody = document.querySelector('#appointmentTable tbody');
            tbody.innerHTML = '';
            
            // Layout of table to receive information from Java and send it back to HTML table
            data.forEach(appointment => {
                const row = document.createElement('tr');
                row.innerHTML = `
                        <td>${appointment.appointmentId}</td>
                        <td>${appointment.patientId}</td>
                        <td>${appointment.procedureId}</td>
                        <td>${appointment.procedureOccurrences}</td>
                        <td>${appointment.dentistId}</td>
                        <td>${appointment.dentalHygienistId}</td>
                        <td>${appointment.dentalAssistantId}</td>
                        <td>${appointment.dentalSurgeonId}</td>
                        <td>${appointment.appointmentDate}</td>
                        <td>${appointment.appointmentTime}</td>
                        <td>${appointment.notes}</td>
                        <td>${appointment.canceled}</td>
                `;
                tbody.appendChild(row);
            });
        })
        .catch(error => {
            console.error('Error fetching appointments:', error);
        });
}

// Function that marks appointments as canceled or not canceled
function cancelAppointment(event) {
    event.preventDefault();

    // Get the appointmentId for the appointment that will be updated.
    const appointmentId = document.getElementById('cancelAppointmentId').value;
    const form = new FormData(document.getElementById('addAppointmentForm'));

    // Validation for field in addAppointmentForm.
    const isCanceled = form.get('canceled');
    if (isCanceled && !validateCanceled(isCanceled)) {
        alert('Please enter a valid value for "Canceled" (true/false).');
        return;
    }

    // Fetches the POST method to update isCanceled for appointment by appointmentId.
    fetch(`cancelAppointment?appointmentId=${appointmentId}`, {
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
        // Refreshes the table, sends message, reset forms, and hide update popup.
        fetchAppointments(); 
        alert('Appointment: ' + appointmentId + ' updated successfully.');
        document.getElementById('cancelAppointmentForm').reset();
        document.getElementById('cancelAppointmentPrompt').style.display = 'none';
        document.getElementById('addAppointmentForm').reset();
    })
    .catch(error => {
        console.error('Error marking appointment:', error);
        alert('Error marking appointment. Please try again.');
    });
}