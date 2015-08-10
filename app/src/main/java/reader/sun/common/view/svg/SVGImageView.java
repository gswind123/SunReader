package reader.sun.common.view.svg;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PictureDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import reader.sun.sunreader.R;

public class SVGImageView extends ImageView {

    private int svgSrc;
    private int svgPaintColor = -1;

    public SVGImageView(Context context) {
        super(context);
    }

    @SuppressLint("NewApi")
    public SVGImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context, attrs);
        if (svgSrc == -1) {
            return;
        }
        setSvgSrc(svgSrc, context);

    }

    private PictureDrawable getSvgDrawable(int resId, Context context) {
        SVGBuilder builder = new SVGBuilder().readFromResource(context.getResources(), resId);
        if (svgPaintColor != -1) {
            builder = builder.setColorSwap(-1, svgPaintColor);
        }
        SVG s = builder.build();
        PictureDrawable pd = new PictureDrawable(s.getPicture());
        return pd;
    }

    private void getAttrs(Context context, AttributeSet attrs)
    {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SVGViewAttr);
        svgSrc = ta.getResourceId(R.styleable.SVGViewAttr_svgSrc, -1);
        //如果没有被setSvgPaintColor设置过颜色，则去读xml中的颜色。
        if (svgPaintColor == -1)
        {
            svgPaintColor = ta.getColor(R.styleable.SVGViewAttr_svgPaintColor, -1);
        }

        ta.recycle();
    }

    @SuppressLint("NewApi")
    public void setSvgSrc(int svgSrcId, Context context)
    {
        Drawable drawables[] = new Drawable[] {
                getSvgDrawable(svgSrcId, context)
        };
        LayerDrawable layered = new LayerDrawable(drawables);
        super.setImageDrawable(layered);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public int getSvgPaintColor() {
        return svgPaintColor;
    }

    public void setSvgPaintColor(int svgPaintColor) {
        this.svgPaintColor = svgPaintColor;
    }
}