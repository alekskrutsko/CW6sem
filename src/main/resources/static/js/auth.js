const enter = document.querySelector(".enter");
const register = document.querySelector(".register");

const signIn = document.querySelector(".sign-in");
const signUp = document.querySelector(".sign-up");

enter.addEventListener("click", showSignIn);

register.addEventListener("click", showSignUp);

document.querySelector("#first-btn").addEventListener('click', saveLocalStorage)
document.querySelector("#second-btn").addEventListener('click', saveLocalStorage)

function saveLocalStorage(e){
    e.preventDefault();
    if(e.target===document.querySelector("#first-btn")){

        localStorage.setItem('login',document.querySelector('#em').value)
        document.forms[0].submit()
    }else if(e.target===document.querySelector("#second-btn")){

        localStorage.setItem('login',document.querySelector('#email').value)
        document.forms[1].submit()
    }
}

function showSignIn(){
    signUp.style.display="none";
    signIn.style.display="flex";
    signIn.style.justifyContent="center";
    signIn.style.flexDirection="column";
}

function showSignUp(){
    signIn.style.display="none";
    signUp.style.display="flex";
    signUp.style.justifyContent="center";
    signUp.style.flexDirection="column";
}
