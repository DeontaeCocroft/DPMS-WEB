document.addEventListener("DOMContentLoaded", function() {
    const form = document.getElementById("loginForm");

    form.addEventListener("submit", function(event) {
        event.preventDefault();

        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        // Simple validation - check if fields are empty
        if (username.trim() === "" || password.trim() === "") {
            alert("Username and password are required!");
            return;
        }

        // Perform AJAX request to validate credentials
        const xhr = new XMLHttpRequest();
        xhr.open("POST", "login", true);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    if (xhr.responseText.trim() === "SUCCESS") {
                        window.location.href = "main_menu.html";
                    } else {
                        const errorMessage = document.getElementById("errorMessage");
                        errorMessage.textContent = "Incorrect username or password. Please try again.";
                        errorMessage.style.display = "block";
                    }
                } else {
                    const errorMessage = document.getElementById("errorMessage");
                    errorMessage.textContent = "An error occurred. Please try again later.";
                    errorMessage.style.display = "block";
                }
            }
        };
        xhr.send("username=" + encodeURIComponent(username) + "&password=" + encodeURIComponent(password));
    });
});


