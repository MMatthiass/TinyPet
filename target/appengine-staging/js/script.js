https://unpkg.com/mithril/mithril.js

var m = require("mithril");
var elem = document.getElementById("contenu");

m.route(elem, "/", {

	   		 '/': PetitionBySign,
		})

var Petition = {

	getPetitionBySign: function() {
        return m.request({
            method: "GET",
            url: "http://localhost:8080/_ah/api/petitionApi/v1/getPetitionBySign",
        })
        .then(function(result) {
            Petition.list = result.items
            m.redraw
        })
    },
}

var PetitionBySign = {

	oninit : Petition.getPetitionBySign(),
	 
	view: function() {
	 	return m('div', [
			m('div',{class:'subtitle'},"Liste petition de la plus récente à la plus ancienne"),
		 ])
	 }
	
}

function getPetitionBySignn() {

	$("#title_page").append("Les 15 petitions les plus signées");

	$.ajax({
       url : 'http://localhost:8080/_ah/api/petitionApi/v1/getPetitionBySign',
       type : 'GET',
       dataType : 'json', // On désire recevoir du HTML
       success : function(data){

       		$.each(data.items, function(i, petition) {

				var tags = '';

				$.each(petition.properties.tags, function(i, tag) { 
					tags += tag + ' '; 
				});

				$( "#data_petition" ).append(
					'<div class="card col-3 m-4" style="width: 18rem;">' +
					'<div class="card-body">' +
					'<h5 class="card-title"> ' + petition.properties.titre + ' </h5>' +
					'<h6 class="card-subtitle mb-2 text-muted"> ' + tags + ' </h6>' +
					'<p class="card-text"> ' + petition.properties.description + ' </p>' +
					'<p class="card-text"> Nombre de signataire : ' + petition.properties.compteur + ' </p>' +
					'<p class="card-text"> Date de création : ' + petition.properties.dateCreation + ' </p>' +
					'<a href="#" class="card-link">Voter</a>' +
					'</div>'
				);
			});
           
       }
    });
}



function getAllPetition() {

	$("#title_page").append("Toutes les petitions");

	$.ajax({
       url : 'http://localhost:8080/_ah/api/petitionApi/v1/getAllPetition',
       type : 'GET',
       dataType : 'json', // On désire recevoir du HTML
       success : function(data){

       		$.each(data.items, function(i, petition) {

				console.log(petition);
			});
           
       }
    });
}

function onSignIn(googleUser) {

	var profile = googleUser.getBasicProfile();
	ProfileUser.infos.id= profile.getId()
	ProfileUser.infos.nom = profile.getName()
	ProfileUser.infos.urlImage = profile.getImageUrl()
	ProfileUser.infos.email = profile.getEmail()
	ProfileUser.infos.token = googleUser.getAuthResponse().id_token;

	document.getElementById("login").classList.add("d-none");
	document.getElementById("logout").classList.remove("d-none");

	document.getElementById("create_petition").classList.remove("d-none");
	document.getElementById("display_petition").classList.remove("d-none");
	document.getElementById("display_vote").classList.remove("d-none");


}

function signOut() {

	var auth2 = gapi.auth2.getAuthInstance();
	console.log(auth2);
	auth2.signOut().then(function () {
		console.log('User signed out.');
		ProfileUser.infos.id= ""
		ProfileUser.infos.nom = ""
		ProfileUser.infos.urlImage = ""
		ProfileUser.infos.email = ""
		ProfileUser.infos.token = ""
	});

	document.getElementById("login").classList.remove("d-none");
	document.getElementById("logout").classList.add("d-none");

		document.getElementById("create_petition").classList.add("d-none");
	document.getElementById("display_petition").classList.add("d-none");
	document.getElementById("display_vote").classList.add("d-none");

}

var ProfileUser = 
{
	infos:
	{
		"id":"",
		"nom":"",
		"email":"",
		"urlImage":"",
		"token":"",
	}
}
