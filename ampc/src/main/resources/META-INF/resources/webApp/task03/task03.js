$(function(){  
    $("#checkall").click(function(){   
        //第一种方法 全选全不选  
        if(this.checked){   
            $("input[name='check1']:checkbox").attr('checked',true);   
        }else{   
            $("input[name='check1']:checkbox").attr('checked',false);    
        }  
        //第二种方法 全选全不选   
        //$('[name=check1]:checkbox').attr('checked',this.checked);//checked为true时为默认显示的状态   
    });  
    $("#checkrev").click(function(){  
        //实现反选功能  
        $('[name=check1]:checkbox').each(function(){  
            this.checked=!this.checked;  
        });  
    });   
}); 