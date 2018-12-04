var expression=/\s+/g;

$(init);

function init()
{
}

function signUpUser() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    var email = document.getElementById("emailId").value;
    var phone = document.getElementById("phone").value;

    emailRE = /^.+@.+\..{2,4}$/;
    if (email!="" && !email.match(emailRE)){
        window.alert("Invalid email address. " + "Should be xxxxx@xxxxx.xxx\n");
        return false;
    }

    phoneRE="[(][0-9]{3}[)] [0-9]{3}-[0-9]{4}";
    if (phone!="" && !phone.match(phoneRE)){
        window.alert("Invalid phone number. " + "Should be (xxx) xxx-xxxx\n");
        return false;
    }

    if (expression.test(password) || expression.test(username) || expression.test(email)) {
        alert("Whitespaces are not allowed!");
    }


    if (username == "") {
        window.alert("Please enter your username");
        username.focus();
        return false;
    }

    if (password == "") {
        window.alert("Please enter your password");
        password.focus();
        return false;
    }

    const data = {
        username: username,
        email: email,
        password:password,
        phone:phone
    };

    fetch("/signupUser", {
        method : "POST",
        body: JSON.stringify(data),
        headers:{
            'Content-Type': 'application/json'
        }
    })
        .then(r => r.json())
        .then(response => {
             if (response.message === "Success") {
                window.location = "/login";

              }
          else {
                   window.alert("Unable to SignUp!");
             }
           console.log('Success:', JSON.stringify(response));
          }).catch(error => {
                   console.error('Error:', error)
         });



    return true;
}