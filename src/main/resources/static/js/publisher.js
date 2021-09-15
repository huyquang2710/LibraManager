const search = () => {
    let query = $("#search-input").val();

    if(query == "") {
        $(".search-result").hide();
    } else {
        //search
        console.log(query);


        //sending request to server
        let url = `http://localhost:8080/search/publisher/${query}`;
        fetch(url) 
            .then((response) => {
                return response.json();
            })
            .then((data) => {
                //data... 
                console.log(data);

                let text = `<div class='list-group'>`;

                data.forEach((publisher) => {
                    text += `<a href='/admin/publisher/findById/${publisher.id}' class='list-group-item list-group-item-action'> ${publisher.name} </a>`;
                });

                text += `</div>`;

                $(".search-result").html(text);
                $(".search-result").show();
            });
        $(".search-result").show();
    }
}
function chooseFile(fileInput) {
    if(fileInput.files && fileInput.files[0]) {
        var reader = new FileReader();

        reader.onload = function(e){
            $('#image').attr('src', e.target.result);
        }
        reader.readAsDataURL(fileInput.files[0]);
    }
}

function reset() {
  document.getElementById("feedback_form").reset();
}
// Search 
function dropDown(event){
		    var val = $("#search").val();
		    if(val.length > 1){
		    	$.ajax({
		            type: "GET",
		            url: "/publisher/search?value="+val,
		            processData: false,// prevent jQuery from automatically
		            // transforming the data into a query string
		            contentType: false,
		            cache: false,
		            timeout: 6000,
		            success: function (data) {
		            	$('.dropdown-toggle').dropdown();
		            	$("#dropdown-container").empty();
		            	$("#dropdown-container").append(data);
		            	
		            },
		    	  error: function (e) {

		              console.log("ERROR : ", e);
		             
		          }
		      });
		    	
		    }
			
		}
