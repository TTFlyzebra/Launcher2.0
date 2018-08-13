<?php

namespace Home\Controller;

use Think\Controller;

class IndexController extends Controller {
	public function index() {
		// $products=D('ProductAll')->relation(true)->limit('0,10')->select();
		// $this->products=$products;
		
		// $shops =D('ShopAll')->relation(true)->limit('4,5')->select();
		// $this->shops=$shops;
		// echo "<br><br><br><br>";
		// $this->display();
		$tabJson = array (
				array (
						'name' => '主页',
						'color1' => '#00FF00',
						'color2' => '#FFFFFF' 
				) ,
				array (
						'name' => '推荐',
						'color1' => '#00FF00',
						'color2' => '#FFFF00'
				),
				array (
						'name' => '点播',
						'color1' => '#00FF00',
						'color2' => '#FFFF00'
				)
		);
		
		echo json_encode($tabJson);
	}
	
	public function item(){
		$tabJson = array (
				array (
						'left' =>100,
						'top' =>200,
						'width' => 287,
						'height'=>408,
						'imgurl'=>"",
						'animation'=>""
				) ,
				array (
						'left' =>380,
						'top' =>200,
						'width' => 575,
						'height'=>269,
						'imgurl'=>"",
						'animation'=>""
				),
				array (
						'left' =>970,
						'top' =>200,
						'width' => 211,
						'height'=>202,
						'imgurl'=>"",
						'animation'=>""
				),
				array (
						'left' =>380,
						'top' =>475,
						'width' => 286,
						'height'=>134,
						'imgurl'=>"",
						'animation'=>""
				),
				array (
						'left' =>670,
						'top' =>475,
						'width' => 286,
						'height'=>134,
						'imgurl'=>"",
						'animation'=>""
				),
				array (
						'left' =>970,
						'top' =>410,
						'width' => 287,
						'height'=>408,
						'imgurl'=>"",
						'animation'=>""
				)
		);
		
		echo json_encode($tabJson);
	}
}