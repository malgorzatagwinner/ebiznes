async function getRequest(url, data, method='GET'){
	const result = await fetch(url, {
       	mode: 'cors',
		headers:{
		      'Accept': 'application/json',
		      'Content-type': 'application/json',
		},
		method,
		body: data ? JSON.stringify(data): undefined,});
	return await result.json();
	
}

const endpoint = (

	window.location.hostname == "localhost" || window.location.hostname.startsWith('127.')
	? 'http://localhost:9000/api'
	: 'https://ebiznes--2021.azurewensites.net/api'

);
export {getRequest, endpoint}
