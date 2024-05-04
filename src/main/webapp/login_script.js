// Deontae Cocroft | 2024 May 4th
window.onload = function() {
    document.getElementById('loginForm').addEventListener('submit', login);
}

// Function to validate login information through database.
function login(event) {
    event.preventDefault();

    const form = document.getElementById("loginForm");
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    // Validation to check if fields are empty
    if (username.trim() === "" || password.trim() === "") {
        alert("Username and password are required!");
        return;
    }

    // POST request to the servlet to validate credentials. 
    fetch('login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'username=' + encodeURIComponent(username) + '&password=' + encodeURIComponent(password)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('An error occurred. Please try again later.');
            }
            return response.text();
        })

        // If success comes back from Servlet user is redirected to main menu. 
        .then(data => {
            if (data.trim() === "SUCCESS") {
                window.location.href = "main_menu.html";
            } else {
                const errorMessage = document.getElementById("errorMessage");
                errorMessage.textContent = "Incorrect username or password. Please try again.";
                errorMessage.style.display = "block";
            }
        })
        .catch(error => {
            console.error('An error occurred:', error);
            const errorMessage = document.getElementById("errorMessage");
            errorMessage.textContent = "An error occurred. Please try again later.";
            errorMessage.style.display = "block";
        });
}


