const toggleSidebar=()=>{
	//true band karna hai
	if($(".sidebar").is(":visible")){
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
	}else{
		//false open karna hai
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","20%");
	}
}

const search=()=>{
	let query=$("#seaech-input").val();
		if(query=='')
		{
			$(".search-result").hide();
		}else{
			//search
			//sending request to server
			let url=`http://localhost:8080/search/${query}`;
			
			fetch(url)
			.then((reponse)=>{
				return reponse.json();
			})
			.then((data)=>{
				console.log(data);
				let text=`<div class='list-group'>`
					
					data.forEach((contact)=>{
						text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'>${contact.name}</a>`
					});
					
					text+=`</div>`
						
					$(".search-result").html(text);	
					$(".search-result").show();
			})
			$(".search-result").show();
		}
}